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

	// --- Speed & Scaling (Merged from first code logic) ---
	@RegisterProperty
	var baseSpeed = 850.0
	private var currentSpeed = 850.0
	private var scaleFactor = 1.0
	private val referenceWidth = 1920.0 // The standard resolution width

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

	// --- Mobile Input State ---
	private var isLeftPressed = false
	private var isRightPressed = false

	// --- Squash & Stretch (Juice) ---
	private val originalBaseScale = Vector2(4.0, 4.0) // Your intended sprite scale
	private var baseScale = originalBaseScale
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
		// Connect to viewport size changes for real-time responsiveness
		getViewport()?.sizeChanged?.connect(this, Basket::adjustToScreen)
		// Initial setup
		adjustToScreen()
		basketSprite = getNodeOrNull("Sprite2D") as? Sprite2D
		loadBasketTextureMap()
		updateBasketVisuals()

		// Connect collision signal
		bodyEntered.connect(this, Basket::onBodyEntered)
		GD.print("Basket: Ready with dynamic scaling and mobile support")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		handleMovement(delta)
		updateAnimations(delta)
	}

	/**
	 * Logic merged from the first code: Adjusts scale and speed based on screen width.
	 * Ensures the game feels the same on a small phone and a large monitor.
	 * 
	 * This implements responsive scaling by:
	 * 1. Calculating scale factor from viewport width relative to 1920px standard
	 * 2. Scaling basket sprite accordingly
	 * 3. Adjusting movement speed proportionally
	 * 4. Maintaining collision box consistency
	 */
	@RegisterFunction
	fun adjustToScreen() {
		screenSize = getViewportRect().size

		// Calculate the ratio compared to 1920p reference width
		scaleFactor = screenSize.x / referenceWidth

		// Adjust speed dynamically so player covers the same screen % per second
		currentSpeed = baseSpeed * scaleFactor

		// Adjust visual base scale to match the screen density
		// This ensures the basket size is proportional to screen dimensions
		baseScale = Vector2(originalBaseScale.x * scaleFactor, originalBaseScale.y * scaleFactor)
		targetScale = baseScale
		scale = baseScale
		
		GD.print("Basket: Screen adjusted - Size: $screenSize, Scale Factor: $scaleFactor, Current Speed: $currentSpeed")
	}

	/**
	 * Handles unified input from both keyboard and mobile controls.
	 */
	private fun handleMovement(delta: Double) {
		// Keyboard: traditional ui_to_left/ui_to_right actions
		val keyboardInputX = (if (Input.isActionPressed("ui_to_right")) 1.0 else 0.0) -
				(if (Input.isActionPressed("ui_to_left")) 1.0 else 0.0)

		// Mobile: button press state flags
		val mobileInputX = (if (isRightPressed) 1.0 else 0.0) - (if (isLeftPressed) 1.0 else 0.0)

		// Unified input: combine both sources
		val inputX = keyboardInputX + mobileInputX

		// Use currentSpeed (scaled) for consistent movement across resolutions
		velocity = if (inputX != 0.0) Vector2(inputX, 0.0).normalized() * currentSpeed else Vector2.ZERO

		// Apply delta-based movement
		position += velocity * delta

		// Screen boundary clamping
		val halfWidth = getBasketHalfWidth()
		position = Vector2(position.x.coerceIn(halfWidth, screenSize.x - halfWidth), position.y)

		// Visual lean based on movement direction
		targetRotation = -velocity.x.sign * leanAmount
	}

	private fun updateAnimations(delta: Double) {
		// Rotation Lean Lerp
		currentRotation = lerp(currentRotation, targetRotation, leanSpeed * delta)
		rotation = currentRotation.toFloat()

		// Squash and Stretch State Machine
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

		// Smoothly transition scale changes
		currentScale = Vector2(lerp(currentScale.x, targetScale.x, 12.0 * delta), lerp(currentScale.y, targetScale.y, 12.0 * delta))
		scale = currentScale
	}

	@RegisterFunction
	fun onBodyEntered(body: Node) {
		val apple = body as? BaseApple ?: return
		val points = apple.getPoints()

		// Update apple count and cycle visuals (0-8 range)
		appleCount = (appleCount + if (points > 0) 1 else -1).coerceIn(0, 8)
		updateBasketVisuals()

		// Trigger visual impact feedback
		startSquash()

		// Emit signal for UI/GameManager
		appleCollected.emit(points)
		apple.queueFree()
	}

	// --- Mobile Control Handlers ---

	@RegisterFunction
	fun onMobileLeftDown() { isLeftPressed = true }

	@RegisterFunction
	fun onMobileLeftUp() { isLeftPressed = false }

	@RegisterFunction
	fun onMobileRightDown() { isRightPressed = true }

	@RegisterFunction
	fun onMobileRightUp() { isRightPressed = false }

	// --- Visual Helpers ---

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
		// Calculate collision width: sprite is 328px wide * 0.2 scale * current scale factor
		// 328 * 0.2 = 65.6px base, then multiplied by the responsive scale
		return (328.0 * 0.2 * scaleFactor / 2.0)
	}

	private fun startSquash() {
		impactPhase = ImpactPhase.SQUASH
		impactTimer = squashDuration
		targetScale = Vector2(baseScale.x + (squashAmount * scaleFactor), baseScale.y - (squashAmount * scaleFactor))
	}

	private fun startStretch() {
		impactPhase = ImpactPhase.STRETCH
		impactTimer = stretchDuration
		targetScale = Vector2(baseScale.x - (stretchAmount * scaleFactor), baseScale.y + (stretchAmount * scaleFactor))
	}

	private fun startSettle() {
		impactPhase = ImpactPhase.SETTLE
		impactTimer = settleDuration
		targetScale = baseScale
	}

	private fun lerp(from: Double, to: Double, weight: Double): Double = from + (to - from) * weight.coerceIn(0.0, 1.0)

	@RegisterFunction
	fun resetBasket() {
		appleCount = 0
		updateBasketVisuals()
		getTree()?.reloadCurrentScene()
		velocity = Vector2.ZERO
		rotation = 0f
		adjustToScreen() // Ensure scaling is reset correctly
		isLeftPressed = false
		isRightPressed = false
	}
}
