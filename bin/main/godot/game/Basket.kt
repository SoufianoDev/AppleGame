package godot.game


import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.api.Area2D
import godot.api.CircleShape2D
import godot.api.CollisionShape2D
import godot.api.Input
import godot.api.Node
import godot.api.RectangleShape2D
import godot.api.ResourceLoader
import godot.api.Sprite2D
import godot.api.Texture2D
import godot.core.Vector2
import godot.core.signal1 
import godot.game.apples.BaseApple
import godot.global.GD
import kotlin.math.sign

@RegisterClass
class Basket : Area2D() {

	@RegisterProperty
	var speed = 850.0

	// Parameter name goes in the annotation, and use signal1 for 1 argument
	@RegisterSignal("points")
	val appleCollected by signal1<Int>()

	// --- Visual & State Management ---
	private var appleCount = 0
	private var screenSize = Vector2.ZERO
	private val textureMap = HashMap<Int, Texture2D>()
	private var basketSprite: Sprite2D? = null

	// --- Movement & Rotation ---
	private var velocity = Vector2.ZERO
	private var targetRotation = 0.0
	private var currentRotation = 0.0

	@RegisterProperty
	var leanAmount = 0.15
	@RegisterProperty
	var leanSpeed = 8.0

	// --- Squash & Stretch (Juice) ---
	private val baseScale = Vector2(4.0, 4.0)
	private var currentScale = baseScale
	private var targetScale = baseScale

	@RegisterProperty
	var squashAmount = 0.35
	@RegisterProperty
	var squashDuration = 0.10
	@RegisterProperty
	var stretchAmount = 0.22
	@RegisterProperty
	var stretchDuration = 0.12
	@RegisterProperty
	var settleDuration = 0.18

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

		basketSprite = getNodeOrNull("Sprite2D") as? Sprite2D
		loadBasketTextureMap()
		updateBasketVisuals()

		// Connect the engine's built-in signal to our handler
		bodyEntered.connect(this, Basket::onBodyEntered)
		GD.print("Basket ready")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		handleMovement(delta)
		updateAnimations(delta)
	}

	private fun handleMovement(delta: Double) {
		val inputX = (if (Input.isActionPressed("ui_to_right")) 1.0 else 0.0) -
				(if (Input.isActionPressed("ui_to_left")) 1.0 else 0.0)

		velocity = if (inputX != 0.0) {
			Vector2(inputX, 0.0).normalized() * speed
		} else {
			Vector2.ZERO
		}

		position += velocity * delta

		// Screen boundary clamping
		val halfWidth = getBasketHalfWidth()
		position = Vector2(
			x = position.x.coerceIn(halfWidth, screenSize.x - halfWidth),
			y = position.y
		)

		// Calculate visual lean based on movement direction
		targetRotation = -velocity.x.sign * leanAmount
	}

	private fun updateAnimations(delta: Double) {
		// Rotation Lean
		currentRotation = lerp(currentRotation, targetRotation, leanSpeed * delta)
		rotation = currentRotation.toFloat()

		// Squash and Stretch State Machine
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

		// Smoothly interpolate scale changes
		currentScale = Vector2(
			lerp(currentScale.x, targetScale.x, 12.0 * delta),
			lerp(currentScale.y, targetScale.y, 12.0 * delta)
		)
		scale = currentScale
	}

	@RegisterFunction
	fun onBodyEntered(body: Node) {
		val apple = body as? BaseApple ?: return
		val points = apple.getPoints()

		// Update visual state (0-8 apples displayed)
		when {
			points > 0 -> appleCount += 1
			points < 0 -> appleCount -= 1
		}
		appleCount = appleCount.coerceIn(0, 8)

		updateBasketVisuals()
		startSquash()

		// Emit the custom signal with the points value
		appleCollected.emit(points)

		apple.queueFree()
	}

	private fun loadBasketTextureMap() {
		val baseBasketPath = "res://src/main/resources/assets/basket/basket_"
		for (i in 0..8) {
			val path = "$baseBasketPath$i.png"
			try {
				val resource = ResourceLoader.load(path) as? Texture2D
				if (resource != null) {
					textureMap[i] = resource
				}
			} catch (e: Exception) {
				GD.printErr("Failed to load basket texture: $path")
			}
		}
	}

	private fun updateBasketVisuals() {
		basketSprite?.texture = textureMap[appleCount]
	}

	private fun getBasketHalfWidth(): Double {
		val col = getNodeOrNull("CollisionShape2D") as? CollisionShape2D
		if (col != null) {
			val shape = col.shape
			if (shape is RectangleShape2D) return (shape.size.x * 0.5) * globalScale.x
			if (shape is CircleShape2D) return shape.radius * globalScale.x
		}
		return 64.0 * globalScale.x // Default fallback
	}

	private fun startSquash() {
		impactPhase = ImpactPhase.SQUASH
		impactTimer = squashDuration
		targetScale = Vector2(baseScale.x + squashAmount, baseScale.y - squashAmount)
	}

	private fun startStretch() {
		impactPhase = ImpactPhase.STRETCH
		impactTimer = stretchDuration
		targetScale = Vector2(baseScale.x - stretchAmount, baseScale.y + stretchAmount)
	}

	private fun startSettle() {
		impactPhase = ImpactPhase.SETTLE
		impactTimer = settleDuration
		targetScale = baseScale
	}

	private fun lerp(from: Double, to: Double, weight: Double): Double {
		return from + (to - from) * weight.coerceIn(0.0, 1.0)
	}

	/**
	 * Resets the basket to its initial state.
	 * Called when gameplay restarts to show empty basket (basket_0).
	 */
	@RegisterFunction
	fun resetBasket() {
		appleCount = 0
		updateBasketVisuals()
		velocity = Vector2.ZERO
		targetRotation = 0.0
		currentRotation = 0.0
		rotation = 0f
		impactPhase = ImpactPhase.IDLE
		impactTimer = 0.0
		currentScale = baseScale
		targetScale = baseScale
		scale = baseScale
		GD.print("Basket: Reset to empty state (basket_0)")
	}
}
