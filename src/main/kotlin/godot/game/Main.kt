package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Node2D
import godot.api.PackedScene
import godot.api.ResourceLoader
import godot.api.Label
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
	var spawnInterval = 1.5

	@RegisterProperty
	var gameDuration = 60.0

	private var gameTimer = 0.0
	private var isGameOver = false

	private var timerLabel: Label? = null

	@RegisterFunction
	override fun _ready() {
		GD.print("🎮 Main scene ready!")

		// Init Apples
		redAppleScene = ResourceLoader.load("res://scenes\\red_apple.tscn") as? PackedScene
		greenAppleScene = ResourceLoader.load("res://scenes\\green_apple.tscn") as? PackedScene
		badAppleScene = ResourceLoader.load("res://scenes\\bad_apple.tscn") as? PackedScene

		// Debug
		GD.print("Red: ${redAppleScene != null}")
		GD.print("Green: ${greenAppleScene != null}")
		GD.print("Bad: ${badAppleScene != null}")

		timerLabel = getNodeOrNull("CanvasLayer2/TimerLabel".asNodePath()) as? Label
		gameTimer = gameDuration
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
		if (spawnTimer >= spawnInterval) {
			spawnRandomApple()
			spawnTimer = 0.0
		}
	}

	@RegisterFunction
	fun spawnRandomApple() {
		val random = Random.nextDouble(0.0, 100.0)

		val (scene, type) = when {
			random < 60.0 -> Pair(redAppleScene, "Red")
			random < 85.0 -> Pair(greenAppleScene, "Green")
			else -> Pair(badAppleScene, "Bad")
		}

		GD.print("Spawning $type apple (random: $random)")

		if (scene == null) {
			GD.print("ERROR: $type apple scene is null!")
			return
		}

		val instance = scene.instantiate()

		if (instance == null) {
			GD.print("ERROR: instantiate() returned null for $type!")
			return
		}

		GD.print("Instance type: ${instance::class.simpleName}")

		val apple = instance as? BaseApple

		if (apple == null) {
			GD.print("ERROR: Cannot cast to BaseApple! Instance is: ${instance::class.simpleName}")
			instance.queueFree() // Free memory
			return
		}

		val screenWidth = getViewportRect().size.x
		val randomX = Random.nextDouble(100.0, screenWidth - 100.0)
		apple.position = Vector2(randomX, -100.0)

		addChild(apple)
		GD.print("$type apple spawned successfully")
	}

	@RegisterFunction
	fun gameOver() {
		isGameOver = true
		GD.print("🎮 GAME OVER!")
	}
}
