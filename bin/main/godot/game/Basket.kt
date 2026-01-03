package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.*
import godot.core.Vector2
import godot.core.asNodePath
import godot.game.apples.BaseApple
import godot.global.GD
import kotlin.math.sign

@RegisterClass
class Basket : Area2D() {

	@RegisterProperty
	var speed = 850.0

	@RegisterProperty
	var score = 0

	private var appleCount = 0

	private var screenSize = Vector2.ZERO
	private var scoreLabel: Label? = null

	// Texture Storage
	private val textureMap = HashMap<Int, Texture2D>()
	private var basketSprite: Sprite2D? = null

	// Movement / inertia
	private var velocity = Vector2.ZERO
	private var targetRotation = 0.0
	private var currentRotation = 0.0

	// Base Scale
	private val baseScale = Vector2(4.0, 4.0)
	private var currentScale = baseScale
	private var targetScale = baseScale

	@RegisterProperty
	var leanAmount = 0.15

	@RegisterProperty
	var leanSpeed = 8.0

	// Squash / Stretch
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

		// Get Basket Sprite
		basketSprite = getNodeOrNull("Sprite2D") as? Sprite2D

		loadBasketTextureMap()

		// Set initial empty basket
		updateBasketVisuals()

		scoreLabel = getParent()?.getNodeOrNull("CanvasLayer/ScoreLabel".asNodePath()) as? Label
		updateScoreUI()

		bodyEntered.connect(this, Basket::onBodyEntered)

		GD.print("Basket ready - ScreenSize=$screenSize")
		GD.print("Loaded ${textureMap.size} basket textures")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		handleMovement(delta)
		updateAnimations(delta)
	}

	// ================= MOVEMENT =================

	private fun handleMovement(delta: Double) {
		val inputX =
			(if (Input.isActionPressed("ui_to_right")) 1.0 else 0.0) - (if (Input.isActionPressed("ui_to_left")) 1.0 else 0.0)

		velocity = if (inputX != 0.0) {
			Vector2(inputX, 0.0).normalized() * speed
		} else {
			Vector2.ZERO
		}

		position += velocity * delta

		val halfWidth = getBasketHalfWidth()
		position = Vector2(
			x = position.x.coerceIn(halfWidth, screenSize.x - halfWidth), y = position.y
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
			lerp(currentScale.x, targetScale.x, 12.0 * delta), lerp(currentScale.y, targetScale.y, 12.0 * delta)
		)

		scale = currentScale
	}

	// ================= COLLISION =================

	@RegisterFunction
	fun onBodyEntered(body: Node) {
		val apple = body as? BaseApple ?: return

		val points = apple.getPoints()

		// Update Score
		score += points
		score = score.coerceAtLeast(0)

		// Update visual apple count
		when {
			points > 0 -> appleCount += 1
			points < 0 -> appleCount -= 1
		}

		// Clamp Between 0 & 8
		appleCount = appleCount.coerceIn(0, 8)

		// Update Visuals
		updateBasketVisuals()
		updateScoreUI()
		startSquash()

		// Debug
		GD.print("AppleCount: $appleCount | Score: $score | Type: ${apple::class.simpleName}")

		apple.queueFree()
	}

	// ================= TEXTURE SYSTEM =================

	/**
	 * Load All Basket Textures Into Memory
	 */
	private fun loadBasketTextureMap() {
		val baseBasketPath = "res://src/main/resources/assets/basket/basket_"

		val emptyBasketPath = "${baseBasketPath}0.png"
		try {
			val loadedResource = ResourceLoader.load(emptyBasketPath)
			val emptyTexture = loadedResource as? Texture2D

			if (emptyTexture != null) {
				textureMap[0] = emptyTexture
				GD.print("Loaded: $emptyBasketPath")
			} else {
				GD.print("Optional empty basket not found or invalid type at: $emptyBasketPath (Using editor default)")
			}
		} catch (e: Exception) {
			GD.printErr("Exception while loading optional empty basket: ${e.message}")
		}

		for (i in 1..8) {
			val path = "${baseBasketPath}$i.png"
			try {
				val loadedResource = ResourceLoader.load(path)
				val texture = loadedResource as? Texture2D

				if (texture != null) {
					textureMap[i] = texture
					GD.print("Loaded: $path")
				} else {
					GD.printErr("Failed to load (file missing or invalid type): $path")
				}
			} catch (e: Exception) {
				GD.printErr("Exception occurred while loading \"basket_$i\" at path $path: ${e.message}\n") }
		}
	}

	/**
	 * Update basket sprite texture based on appleCount
	 */
	private fun updateBasketVisuals() {
		if (basketSprite == null) {
			GD.printErr("basketSprite is null!")
			return
		}

		val texture = textureMap[appleCount]

		if (texture != null) {
			basketSprite?.texture = texture
			GD.print("Updated basket visual to count: $appleCount")
		} else {
			GD.print("Texture not found for count: $appleCount (Keeping current texture)")
		}
	}

	// ================= WIDTH CALCULATION =================

	private fun getBasketHalfWidth(): Double {
		val col = getNodeOrNull("CollisionShape2D") as? CollisionShape2D
		if (col != null) {
			when (val shape = col.shape) {
				is RectangleShape2D -> {
					return (shape.size.x * 0.5) * globalScale.x
				}

				is CircleShape2D -> {
					return shape.radius * globalScale.x
				}
			}
		}

		val sprite = getNodeOrNull("Sprite2D") as? Sprite2D
		if (sprite != null && sprite.texture != null) {
			val texSize = sprite.texture!!.getSize()
			return (texSize.x * 0.5) * sprite.scale.x * globalScale.x
		}

		GD.printErr("Basket width fallback used!")
		return 100.0 * globalScale.x
	}

	// ================= IMPACT =================

	private fun startSquash() {
		impactPhase = ImpactPhase.SQUASH
		impactTimer = squashDuration
		targetScale = Vector2(
			baseScale.x + squashAmount, baseScale.y - squashAmount
		)
	}

	private fun startStretch() {
		impactPhase = ImpactPhase.STRETCH
		impactTimer = stretchDuration
		targetScale = Vector2(
			baseScale.x - stretchAmount, baseScale.y + stretchAmount
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
