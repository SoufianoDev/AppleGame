package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.api.*
import godot.core.Vector2
import godot.core.signal1
import godot.game.apples.BaseApple
import godot.global.GD
import kotlin.math.sign

@RegisterClass
class Basket : Area2D() {

	@RegisterProperty
	var speed = 850.0

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

	@RegisterProperty var leanAmount = 0.15
	@RegisterProperty var leanSpeed = 8.0

	// --- Mobile Input State (NEW) ---
	// Tracks whether mobile control buttons are currently pressed
	// Used in handleMovement() to generate movement input
	private var isLeftPressed = false
	private var isRightPressed = false

	// --- Squash & Stretch (Juice) ---
	private val baseScale = Vector2(4.0, 4.0)
	private var currentScale = baseScale
	private var targetScale = baseScale

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
		basketSprite = getNodeOrNull("Sprite2D") as? Sprite2D
		loadBasketTextureMap()
		updateBasketVisuals()
		bodyEntered.connect(this, Basket::onBodyEntered)
		GD.print("Basket: Ready with mobile controls support")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		handleMovement(delta)
		updateAnimations(delta)
	}

	/**
	 * Handles unified input from both keyboard and mobile controls.
	 *
	 * Input sources:
	 * - Keyboard: ui_to_left / ui_to_right actions (via Input.isActionPressed)
	 * - Mobile: isLeftPressed / isRightPressed state flags (set by button signals)
	 *
	 * Logic ensures one-touch support: a single finger on either button generates
	 * correct directional input without conflicts. Press-and-hold equals continuous
	 * movement; release equals immediate stop (velocity = ZERO).
	 */
	private fun handleMovement(delta: Double) {
		// Combine keyboard and mobile inputs
		// Keyboard: traditional ui_to_left/ui_to_right actions
		val keyboardInputX = (if (Input.isActionPressed("ui_to_right")) 1.0 else 0.0) -
				(if (Input.isActionPressed("ui_to_left")) 1.0 else 0.0)

		// Mobile: button press state flags
		val mobileInputX = (if (isRightPressed) 1.0 else 0.0) - (if (isLeftPressed) 1.0 else 0.0)

		// Unified input: combine both sources (allows simultaneous input if needed)
		val inputX = keyboardInputX + mobileInputX

		// Convert to velocity (maintains existing animation behavior)
		velocity = if (inputX != 0.0) Vector2(inputX, 0.0).normalized() * speed else Vector2.ZERO

		// Apply delta-based movement
		position += velocity * delta

		// Screen boundary clamping (unchanged)
		val halfWidth = getBasketHalfWidth()
		position = Vector2(position.x.coerceIn(halfWidth, screenSize.x - halfWidth), position.y)

		// Visual lean based on movement direction (unchanged - preserves all juice)
		targetRotation = -velocity.x.sign * leanAmount
	}

	private fun updateAnimations(delta: Double) {
		currentRotation = lerp(currentRotation, targetRotation, leanSpeed * delta)
		rotation = currentRotation.toFloat()

		if (impactPhase != ImpactPhase.IDLE) {
			impactTimer -= delta
			if (impactTimer <= 0.0) {
				when (impactPhase) {
					ImpactPhase.SQUASH -> startStretch()
					ImpactPhase.STRETCH -> startSettle()
					ImpactPhase.SETTLE -> { impactPhase = ImpactPhase.IDLE; targetScale = baseScale }
					else -> {}
				}
			}
		}

		currentScale = Vector2(lerp(currentScale.x, targetScale.x, 12.0 * delta), lerp(currentScale.y, targetScale.y, 12.0 * delta))
		scale = currentScale
	}

	@RegisterFunction
	fun onBodyEntered(body: Node) {
		val apple = body as? BaseApple ?: return
		val points = apple.getPoints()
		appleCount = (appleCount + if (points > 0) 1 else -1).coerceIn(0, 8)
		updateBasketVisuals()
		startSquash()
		appleCollected.emit(points)
		apple.queueFree()
	}

	// ================= MOBILE CONTROLS - INPUT HANDLERS =================

	/**
	 * Called when the LEFT mobile control button is pressed down.
	 * Sets state flag to enable continuous leftward movement in _process().
	 */
	@RegisterFunction
	fun onMobileLeftDown() {
		isLeftPressed = true
	}

	/**
	 * Called when the LEFT mobile control button is released.
	 * Clears state flag to immediately stop leftward movement.
	 */
	@RegisterFunction
	fun onMobileLeftUp() {
		isLeftPressed = false
	}

	/**
	 * Called when the RIGHT mobile control button is pressed down.
	 * Sets state flag to enable continuous rightward movement in _process().
	 */
	@RegisterFunction
	fun onMobileRightDown() {
		isRightPressed = true
	}

	/**
	 * Called when the RIGHT mobile control button is released.
	 * Clears state flag to immediately stop rightward movement.
	 */
	@RegisterFunction
	fun onMobileRightUp() {
		isRightPressed = false
	}

	// ================= TEXTURE & VISUALS (UNCHANGED) =================

	private fun loadBasketTextureMap() {
		for (i in 0..8) {
			val path = "res://src/main/resources/assets/basket/basket_$i.png"
			(ResourceLoader.load(path) as? Texture2D)?.let { textureMap[i] = it }
		}
	}

	private fun updateBasketVisuals() {
		basketSprite?.texture = textureMap[appleCount]
	}

	private fun getBasketHalfWidth(): Double {
		return 64.0 * globalScale.x
	}

	private fun startSquash() { impactPhase = ImpactPhase.SQUASH; impactTimer = squashDuration; targetScale = Vector2(baseScale.x + squashAmount, baseScale.y - squashAmount) }
	private fun startStretch() { impactPhase = ImpactPhase.STRETCH; impactTimer = stretchDuration; targetScale = Vector2(baseScale.x - stretchAmount, baseScale.y + stretchAmount) }
	private fun startSettle() { impactPhase = ImpactPhase.SETTLE; impactTimer = settleDuration; targetScale = baseScale }
	private fun lerp(from: Double, to: Double, weight: Double): Double = from + (to - from) * weight.coerceIn(0.0, 1.0)

	@RegisterFunction
	fun resetBasket() {
		appleCount = 0
		updateBasketVisuals()
		velocity = Vector2.ZERO
		rotation = 0f
		scale = baseScale

		// Reset mobile input state on restart
		isLeftPressed = false
		isRightPressed = false
	}
}
