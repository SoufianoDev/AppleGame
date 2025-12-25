package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Label
import godot.api.Node2D
import godot.api.PackedScene
import godot.api.ResourceLoader
import godot.api.RigidBody2D
import godot.core.StringName
import godot.core.Vector2
import kotlin.random.Random

@RegisterClass
class Main : Node2D() {

	@RegisterProperty
	var appleScene: PackedScene? = null

	private var spawnTimer = 0.0

	@RegisterProperty
	var spawnInterval = 1.5

	@RegisterProperty
	var minSpawnInterval = 0.5

	@RegisterProperty
	var difficultyIncreaseRate = 0.99

	private var scoreLabel: Label? = null
	private var basket: Node2D? = null

	@RegisterFunction
	override fun _ready() {
		appleScene = ResourceLoader.load("res://apple.tscn") as? PackedScene
		scoreLabel = getNode("CanvasLayer/ScoreLabel") as? Label
		basket = getNode("Basket") as? Node2D
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		spawnTimer += delta

		if (spawnTimer >= spawnInterval) {
			spawnApple()
			spawnTimer = 0.0

			if (spawnInterval > minSpawnInterval) {
				spawnInterval *= difficultyIncreaseRate
			}
		}
	}

	@RegisterFunction
	fun spawnApple() {
		val apple = appleScene?.instantiate() as? RigidBody2D ?: return

		addChild(apple)

		val screenWidth = getViewportRect().size.x
		val randomX = Random.nextDouble(100.0, screenWidth - 100.0)

		apple.position = Vector2(randomX, -100.0)

		apple.setName(StringName("Apple_${System.currentTimeMillis()}").toString())
	}
	}
