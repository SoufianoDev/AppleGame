package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.*
import godot.core.*
import godot.game.apples.BaseApple
import godot.global.GD
import kotlin.math.sign

@RegisterClass
class Basket : Area2D() {

	@RegisterProperty
	var speed = 700.0

	@RegisterProperty
	var score = 0

	private var screenSize = Vector2.ZERO
	private var scoreLabel: Label? = null

	// Movement / inertia
	private var velocity = Vector2.ZERO
	private var targetRotation = 0.0
	private var currentRotation = 0.0

	// Base scale
	private val baseScale = Vector2(4.0, 4.0)
	private var currentScale = baseScale
	private var targetScale = baseScale

	@RegisterProperty var leanAmount = 0.15
	@RegisterProperty var leanSpeed = 8.0

	// Squash / Stretch
	@RegisterProperty var squashAmount = 0.35
	@RegisterProperty var squashDuration = 0.10
	@RegisterProperty var stretchAmount = 0.22
	@RegisterProperty var stretchDuration = 0.12
	@RegisterProperty var settleDuration = 0.18

	private enum class ImpactPhase { IDLE, SQUASH, STRETCH, SETTLE }
	private var impactPhase = ImpactPhase.IDLE
	private var impactTimer = 0.0

	@RegisterFunction
	override fun _ready() {
		screenSize = getViewportRect().size
		monitoring = true
		setProcess(true)

		scale = baseScale
		currentScale = baseScale
		targetScale = baseScale

		scoreLabel = getParent()
			?.getNodeOrNull("CanvasLayer/ScoreLabel".asNodePath()) as? Label
		updateScoreUI()

		bodyEntered.connect(this, Basket::onBodyEntered)

		GD.print("Basket ready - ScreenSize=$screenSize")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		handleMovement(delta)
		updateAnimations(delta)
	}

	// ================= MOVEMENT =================

	private fun handleMovement(delta: Double) {
		val inputX =
			(if (Input.isActionPressed("ui_to_right")) 1.0 else 0.0) -
					(if (Input.isActionPressed("ui_to_left")) 1.0 else 0.0)

		velocity = if (inputX != 0.0) {
			Vector2(inputX, 0.0).normalized() * speed
		} else {
			Vector2.ZERO
		}

		position += velocity * delta

		// ✅ CORRECT CLAMP USING REAL WIDTH
		val halfWidth = getBasketHalfWidth()
		position = Vector2(
			x = position.x.coerceIn(halfWidth, screenSize.x - halfWidth),
			y = position.y
		)

		val dirX = velocity.x.sign
		targetRotation = -dirX * leanAmount
	}

	// ================= ANIMATIONS =================

	private fun updateAnimations(delta: Double) {
		currentRotation = lerp(currentRotation, targetRotation, leanSpeed * delta)
		rotation = currentRotation.toFloat()

		if (impactPhase != ImpactPhase.IDLE) {
			impactTimer -= delta
			if (impactTimer <= 0.0) {
				when (impactPhase) {
					ImpactPhase.SQUASH -> startStretch()
					ImpactPhase.STRETCH -> startSettle()
					ImpactPhase.SETTLE -> {
						impactPhase = ImpactPhase.IDLE
						targetScale = baseScale
					}
					else -> {}
				}
			}
		}

		currentScale = Vector2(
			lerp(currentScale.x, targetScale.x, 12.0 * delta),
			lerp(currentScale.y, targetScale.y, 12.0 * delta)
		)

		scale = currentScale
	}

	// ================= COLLISION =================

	@RegisterFunction
	fun onBodyEntered(body: Node) {
		val apple = body as? BaseApple ?: return

		score += apple.getPoints().coerceAtLeast(0)
		updateScoreUI()
		startSquash()

		apple.queueFree()
	}

	// ================= WIDTH CALCULATION =================

	/**
	 * Calculates the REAL half width of the basket on screen
	 */
	private fun getBasketHalfWidth(): Double {

		// 1️⃣ CollisionShape2D (BEST)
		val col = getNodeOrNull("CollisionShape2D") as? CollisionShape2D
		if (col != null) {
			val shape = col.shape
			when (shape) {
				is RectangleShape2D -> {
					return (shape.size.x * 0.5) * globalScale.x
				}
				is CircleShape2D -> {
					return shape.radius * globalScale.x
				}
			}
		}

		// 2️⃣ Sprite2D fallback
		val sprite = getNodeOrNull("Sprite2D") as? Sprite2D
		if (sprite != null && sprite.texture != null) {
			val texSize = sprite.texture!!.getSize()
			return (texSize.x * 0.5) * sprite.scale.x * globalScale.x
		}

		// 3️⃣ Emergency fallback
		GD.printErr("Basket width fallback used!")
		return 100.0 * globalScale.x
	}

	// ================= IMPACT =================

	private fun startSquash() {
		impactPhase = ImpactPhase.SQUASH
		impactTimer = squashDuration
		targetScale = Vector2(
			baseScale.x + squashAmount,
			baseScale.y - squashAmount
		)
	}

	private fun startStretch() {
		impactPhase = ImpactPhase.STRETCH
		impactTimer = stretchDuration
		targetScale = Vector2(
			baseScale.x - stretchAmount,
			baseScale.y + stretchAmount
		)
	}

	private fun startSettle() {
		impactPhase = ImpactPhase.SETTLE
		impactTimer = settleDuration
		targetScale = baseScale
	}

	// ================= UI =================

	private fun updateScoreUI() {
		scoreLabel?.text = "Score: $score"
	}

	private fun lerp(from: Double, to: Double, weight: Double): Double {
		val w = weight.coerceIn(0.0, 1.0)
		return from + (to - from) * w
	}
}
