package godot.game
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.Area2D
import godot.api.Input
import godot.api.Label
import godot.api.Node
import godot.api.Node2D
import godot.core.Callable
import godot.core.StringName
import godot.core.Vector2
import godot.global.GD

@RegisterClass
class Basket : Area2D() {

	@RegisterProperty
	var speed = 700.0

	@RegisterProperty
	var score = 0

	private var screenSize = Vector2.ZERO
	private var scoreLabel: Label? = null

	@RegisterFunction
	override fun _ready() {
		screenSize = getViewportRect().size

		// الحصول على ScoreLabel من Main
		val parent = getParent()
		if (parent != null) {
			scoreLabel = parent.getNode("CanvasLayer/ScoreLabel") as? Label
		}

		// الاتصال بإشارة التصادم
		bodyEntered.connect(Callable(this, StringName("onBodyEntered")))
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		var velocity = Vector2.ZERO

		if (Input.isActionPressed("ui_right")) {
			velocity.x = 1.0
		}
		if (Input.isActionPressed("ui_left")) {
			velocity.x = -1.0
		}

		if (velocity.length() > 0.0) {
			velocity = velocity.normalized() * speed
			position = position + (velocity * delta)

			val halfWidth = 100.0
			position = Vector2(
				position.x.coerceIn(halfWidth, screenSize.x - halfWidth),
				position.y
			)
		}
	}

	@RegisterFunction
	fun onBodyEntered(body: Node) {
		val bodyNode = body as? Node2D
		if (bodyNode != null && bodyNode.name.toString().startsWith("Apple")) {
			score++
			scoreLabel?.text = "Score: $score"
			GD.print("Score: $score")
			bodyNode.queueFree()
		}
	}
}
