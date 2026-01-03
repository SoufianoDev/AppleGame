package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Node2D
import godot.api.PackedScene
import godot.api.ResourceLoader
import godot.api.Label
import godot.api.RigidBody2D
import godot.core.Vector2
import godot.core.asNodePath
import godot.game.apples.BaseApple
import godot.global.GD
import kotlin.random.Random

@RegisterClass
class Main : Node2D() {

	private var redAppleScene: PackedScene? = null
	private var greenAppleScene: PackedScene? = null
	private var badAppleScene: PackedScene? = null

	private var spawnTimer = 0.0

	@RegisterProperty
	var initialSpawnInterval = 1.5

	@RegisterProperty
	var minSpawnInterval = 0.5

	@RegisterProperty
	var difficultyIncreaseRate = 0.98

	private var currentSpawnInterval = 1.5

	@RegisterProperty
	var gameDuration = 60.0

	private var gameTimer = 0.0
	private var isGameOver = false

	private var timerLabel: Label? = null

	// Wave system
	private var applesSpawned = 0
	private var currentWave = 1

	@RegisterProperty
	var applesPerWave = 10

	@RegisterFunction
	override fun _ready() {
		GD.print("🎮 Main scene ready!")

		// Init Apples
		redAppleScene = ResourceLoader.load("res://scenes/red_apple.tscn") as? PackedScene
		greenAppleScene = ResourceLoader.load("res://scenes/green_apple.tscn") as? PackedScene
		badAppleScene = ResourceLoader.load("res://scenes/bad_apple.tscn") as? PackedScene

		// Debug
		GD.print("Red: ${redAppleScene != null}")
		GD.print("Green: ${greenAppleScene != null}")
		GD.print("Bad: ${badAppleScene != null}")

		timerLabel = getNodeOrNull("CanvasLayer2/TimerLabel".asNodePath()) as? Label
		gameTimer = gameDuration
		currentSpawnInterval = initialSpawnInterval
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		if (isGameOver) return

		gameTimer -= delta
		timerLabel?.text = "Time: ${gameTimer.toInt()}"

		if (gameTimer <= 0.0) {
			gameOver()
			return
		}

		spawnTimer += delta
		if (spawnTimer >= currentSpawnInterval) {
			spawnRandomApple()
			spawnTimer = 0.0


			if (currentSpawnInterval > minSpawnInterval) {
				currentSpawnInterval *= difficultyIncreaseRate
			}
		}
	}

	@RegisterFunction
	fun spawnRandomApple() {
		applesSpawned++
		if (applesSpawned > applesPerWave) {
			applesSpawned = 0
			currentWave++
			GD.print("Wave $currentWave started!")
		}

		val (scene, type, points) = selectAppleByWave()

		if (scene == null) {
			GD.print("ERROR: $type apple scene is null!")
			return
		}

		val instance = scene.instantiate()

		if (instance == null) {
			GD.print("ERROR: instantiate() returned null for $type!")
			return
		}

		val apple = instance as? BaseApple

		if (apple == null) {
			GD.print("ERROR: Cannot cast to BaseApple!")
			instance.queueFree()
			return
		}

		// Random Position
		val screenWidth = getViewportRect().size.x
		val randomX = getSmartRandomX(screenWidth)
		apple.position = Vector2(randomX, -100.0)

		if (apple is RigidBody2D) {
			applyRandomPhysics(apple, points)
		}

		addChild(apple)
		GD.print("$type apple spawned at x=$randomX")
	}


	private fun selectAppleByWave(): Triple<PackedScene?, String, Int> {
		val random = Random.nextDouble(0.0, 100.0)

		return when {
			currentWave <= 2 -> {
				when {
					random < 80.0 -> Triple(redAppleScene, "Red", 1)
					random < 95.0 -> Triple(greenAppleScene, "Green", 2)
					else -> Triple(badAppleScene, "Bad", -1)
				}
			}
			currentWave <= 4 -> {
				when {
					random < 60.0 -> Triple(redAppleScene, "Red", 1)
					random < 85.0 -> Triple(greenAppleScene, "Green", 2)
					else -> Triple(badAppleScene, "Bad", -1)
				}
			}
			else -> {
				when {
					random < 50.0 -> Triple(redAppleScene, "Red", 1)
					random < 75.0 -> Triple(greenAppleScene, "Green", 2)
					else -> Triple(badAppleScene, "Bad", -1)
				}
			}
		}
	}

	private var lastSpawnX = 0.0

	private fun getSmartRandomX(screenWidth: Double): Double {
		val margin = 100.0
		val minDistance = 150.0

		var attempts = 0
		var randomX: Double

		do {
			randomX = Random.nextDouble(margin, screenWidth - margin)
			attempts++
		} while (kotlin.math.abs(randomX - lastSpawnX) < minDistance && attempts < 5)

		lastSpawnX = randomX
		return randomX
	}


	private fun applyRandomPhysics(apple: RigidBody2D, points: Int) {

		val gravityMultiplier = when (points) {
			2 -> Random.nextDouble(0.8, 1.0)
			-1 -> Random.nextDouble(1.1, 1.3)
			else -> Random.nextDouble(0.9, 1.1)
		}

		apple.gravityScale = gravityMultiplier.toFloat()

		val lateralImpulse = Random.nextDouble(-50.0, 50.0)
		apple.applyImpulse(Vector2(lateralImpulse, 0.0))

		val angularImpulse = Random.nextDouble(-0.5, 0.5).toFloat()
		apple.applyTorqueImpulse(angularImpulse)
	}

	@RegisterFunction
	fun gameOver() {
		isGameOver = true
		GD.print("🎮 GAME OVER!")
		GD.print("Final Wave: $currentWave")
		GD.print("Final Spawn Interval: $currentSpawnInterval")
	}
}
