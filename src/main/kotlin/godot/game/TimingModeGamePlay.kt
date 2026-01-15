package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.api.*
import godot.core.Vector2
import godot.core.asNodePath
import godot.core.signal1
import godot.game.apples.BaseApple
import godot.global.GD
import kotlin.math.abs
import kotlin.random.Random

@RegisterClass
class TimingModeGameplay : Node2D() {

	// Signals for UI updates - Main.kt will connect to these
	@RegisterSignal
	val scoreUpdated by signal1<String>()

	@RegisterSignal
	val timerUpdated by signal1<String>()

	@RegisterSignal
	val gameOverSignal by signal1<String>()

	private var redAppleScene: PackedScene? = null
	private var greenAppleScene: PackedScene? = null
	private var badAppleScene: PackedScene? = null

	private var scoreController: ScoreController? = null
	private var basket: Basket? = null

	private var isGameActive = true
	private var gameTimer = 0.0
	private var spawnTimer = 0.0
	private var currentSpawnInterval = 1.5
	private var lastSpawnX = 0.0
	private var currentWave = 1
	private var applesSpawned = 0

	@RegisterProperty
	var initialSpawnInterval = 1.8

	@RegisterProperty
	var minSpawnInterval = 0.4

	@RegisterProperty
	var difficultyIncreaseRate = 0.97

	@RegisterProperty
	var gameDuration = 60.0

	@RegisterProperty
	var applesPerWave = 12

	@RegisterFunction
	override fun _ready() {
		initializeGameplay()
	}

	private fun initializeGameplay() {
		scoreController = ScoreController()
		scoreController?.let { addChild(it) }

		loadResources()
		bindNodes()

		resetGameState()
	}

	private fun loadResources() {
		redAppleScene = ResourceLoader.load("res://scenes/red_apple.tscn") as? PackedScene
		greenAppleScene = ResourceLoader.load("res://scenes/green_apple.tscn") as? PackedScene
		badAppleScene = ResourceLoader.load("res://scenes/bad_apple.tscn") as? PackedScene
	}

	private fun bindNodes() {
		basket = getNodeOrNull("Basket".asNodePath()) as? Basket
		if (basket == null) {
			GD.printErr("TimingModeGameplay: Basket not found as child, will try parent path")
		}
	}

	@RegisterFunction
	fun connectBasketSignals() {
		// Try to get Basket from parent (Main) since it's a sibling node
		if (basket == null) {
			basket = getNodeOrNull("../Basket".asNodePath()) as? Basket
		}
		
		if (basket != null) {
			basket?.appleCollected?.connect(this, TimingModeGameplay::onAppleCollected)
			GD.print("TimingModeGameplay: Basket signal connected successfully")
		} else {
			GD.printErr("TimingModeGameplay: Failed to connect Basket signal - Basket node not found")
		}
	}

	private fun resetGameState() {
		isGameActive = true
		gameTimer = gameDuration
		spawnTimer = 0.0
		currentSpawnInterval = initialSpawnInterval
		lastSpawnX = 0.0
		currentWave = 1
		applesSpawned = 0
		scoreController?.resetScore()

		// Emit initial UI values
		timerUpdated.emit("Time: ${gameTimer.toInt()}")
		scoreUpdated.emit("Score: 0")
		
		GD.print("TimingModeGameplay: Game state reset, initial signals emitted")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		if (!isGameActive) return

		updateGameTimer(delta)
		handleSpawning(delta)
	}

	private fun updateGameTimer(delta: Double) {
		gameTimer -= delta
		timerUpdated.emit("Time: ${gameTimer.toInt()}")

		if (gameTimer <= 0.0) {
			finalizeGame()
		}
	}

	private fun handleSpawning(delta: Double) {
		spawnTimer += delta
		if (spawnTimer >= currentSpawnInterval) {
			val burstCount = calculateBurstCount()
			repeat(burstCount) { spawnLogic() }

			spawnTimer = 0.0
			increaseDifficulty()
		}
	}

	private fun calculateBurstCount(): Int {
		val roll = Random.nextDouble()
		return when {
			currentWave > 5 && roll > 0.85 -> 3
			currentWave > 2 && roll > 0.70 -> 2
			else -> 1
		}
	}

	private fun spawnLogic() {
		updateWaveProgress()

		val scene = selectAppleScene() ?: return
		val apple = scene.instantiate() as? BaseApple ?: return

		applyFunPhysics(apple)
		addChild(apple)
	}

	private fun updateWaveProgress() {
		applesSpawned++
		if (applesSpawned >= applesPerWave) {
			applesSpawned = 0
			currentWave++
		}
	}

	private fun selectAppleScene(): PackedScene? {
		val roll = Random.nextDouble(0.0, 100.0)
		return when {
			currentWave <= 2 -> when {
				roll < 85.0 -> redAppleScene
				roll < 97.0 -> greenAppleScene
				else -> badAppleScene
			}
			else -> when {
				roll < 60.0 -> redAppleScene
				roll < 85.0 -> greenAppleScene
				else -> badAppleScene
			}
		}
	}

	private fun applyFunPhysics(apple: BaseApple) {
		val screenWidth = getViewportRect().size.x
		val startX = calculateSmartX(screenWidth)
		apple.position = Vector2(startX, -100.0)

		when {
			apple.getPoints() == 2 -> { // Green: "Floaty & Drifting"
				apple.gravityScale = Random.nextDouble(0.7, 0.9).toFloat()
				apple.applyImpulse(Vector2(Random.nextDouble(-80.0, 80.0), 0.0))
			}
			apple.getPoints() < 0 -> { // Bad: "Fast & Heavy"
				apple.gravityScale = Random.nextDouble(1.4, 1.8).toFloat()
				apple.applyTorqueImpulse(Random.nextDouble(1.0, 2.0).toFloat())
			}
			else -> { // Red: "Classic"
				apple.gravityScale = 1.0f
				apple.applyImpulse(Vector2(Random.nextDouble(-30.0, 30.0), 0.0))
			}
		}
	}

	private fun calculateSmartX(screenWidth: Double): Double {
		var x: Double
		var attempts = 0
		do {
			x = Random.nextDouble(100.0, screenWidth - 100.0)
			attempts++
		} while (abs(x - lastSpawnX) < 180.0 && attempts < 5)
		lastSpawnX = x
		return x
	}

	private fun increaseDifficulty() {
		if (currentSpawnInterval > minSpawnInterval) {
			currentSpawnInterval *= difficultyIncreaseRate
		}
	}

	@RegisterFunction
	fun onAppleCollected(points: Int) {
		if (!isGameActive) return
		scoreController?.addScore(points)
		val newScore = scoreController?.getFormattedScore() ?: "0"
		scoreUpdated.emit("Score: $newScore")
		GD.print("TimingModeGameplay: Apple collected, points=$points, new score=$newScore")
	}

	/**
	 * Explicit cleanup method to properly dispose of gameplay resources.
	 * Called before queueFree() to ensure clean state for restart.
	 *
	 * Cleanup steps (order is important):
	 * 1. Deactivate game state to stop processing
	 * 2. Disconnect basket signal to prevent callbacks to freed instance
	 * 3. Free all spawned apple nodes
	 * 4. Nullify scene and node references for garbage collection
	 */
	@RegisterFunction
	fun cleanupGameplay() {
		GD.print("TimingModeGameplay: Starting cleanup...")

		// Step 1: Deactivate game state
		isGameActive = false
		GD.print("TimingModeGameplay: Game state deactivated")

		// Step 2: Disconnect signals to prevent dangling references
		if (basket != null) {
			try {
				// Disconnect appleCollected signal from this handler
				basket?.appleCollected?.disconnect(this, TimingModeGameplay::onAppleCollected)
				GD.print("TimingModeGameplay: Basket signal disconnected")
			} catch (e: Exception) {
				GD.printErr("TimingModeGameplay: Failed to disconnect basket signal: ${e.message}")
			}
		}

		// Step 3: Free all spawned apple child nodes
		var appleCount = 0
		for (i in getChildCount() - 1 downTo 0) {
			val child = getChild(i)
			if (child is BaseApple) {
				child.queueFree()
				appleCount++
			}
			// Also free ScoreController if it's a child
			if (child is ScoreController) {
				child.queueFree()
			}
		}
		GD.print("TimingModeGameplay: Freed $appleCount apple nodes")

		// Step 4: Nullify references to allow garbage collection
		scoreController = null
		basket = null
		redAppleScene = null
		greenAppleScene = null
		badAppleScene = null
		GD.print("TimingModeGameplay: All references nullified")

		GD.print("TimingModeGameplay: Cleanup complete")
	}

	private fun finalizeGame() {
		isGameActive = false
		val score = scoreController?.getFormattedScore() ?: "0"
		GD.print("TimingModeGameplay: Game over! Score: $score")
		gameOverSignal.emit(score)
	}
}
