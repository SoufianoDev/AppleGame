package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.*
import godot.core.Color
import godot.core.asNodePath
import godot.global.GD

@RegisterClass
class Main : Node2D() {

	private var timerLabel: Label? = null
	private var scoreLabel: Label? = null
	private var basket: Basket? = null
	private var gameOverScene: PackedScene? = null
	private var gameplayInstance: TimingModeGameplay? = null
	private var gameOverScreen: GameOverScreen? = null
	private var gameOverLayer: CanvasLayer? = null

	// Mobile controls (TextureButton nodes under MobileControls CanvasLayer)
	private var leftBtnControl: TextureButton? = null
	private var rightBtnControl: TextureButton? = null

	private var restartPending = false

	@RegisterProperty
	var gameDuration = 60.0

	/**
	 * Default opacity for mobile control buttons (0.0 = fully transparent, 1.0 = fully opaque).
	 * This value is applied when buttons are first bound in _ready().
	 * Can be modified in Godot Inspector.
	 */
	@RegisterProperty
	var defaultMobileOpacity = 0.1

	@RegisterFunction
	override fun _ready() {
		GD.print("Main: Starting game...")
		bindMainUI()
		bindBasket()
		bindMobileControls()
		loadResources()
		startGameplayLifeCycle()
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		if (restartPending && gameplayInstance == null) {
			restartPending = false
			GD.print("Main: Executing deferred restart")
			restartGamePlayLifeCycle()
		}
	}

	private fun bindMainUI() {
		timerLabel = getNodeOrNull("CanvasLayer2/TimerLabel".asNodePath()) as? Label
		scoreLabel = getNodeOrNull("CanvasLayer/ScoreLabel".asNodePath()) as? Label
		GD.print("Main: Timer label bound? ${timerLabel != null}")
		GD.print("Main: Score label bound? ${scoreLabel != null}")
	}

	private fun bindBasket() {
		basket = getNodeOrNull("Basket".asNodePath()) as? Basket
		if (basket == null) GD.printErr("Main: Basket not found in scene tree")
	}

	/**
	 * Binds mobile control TextureButtons to Basket input handlers and applies default opacity.
	 *
	 * Expected scene structure:
	 * - MobileControls (CanvasLayer)
	 *   - LeftBtnControl (TextureButton)
	 *   - RightBtnControl (TextureButton)
	 */
	private fun bindMobileControls() {
		leftBtnControl = getNodeOrNull("MobileControls/LeftBtnControl".asNodePath()) as? TextureButton
		rightBtnControl = getNodeOrNull("MobileControls/RightBtnControl".asNodePath()) as? TextureButton

		if (leftBtnControl != null && rightBtnControl != null && basket != null) {
			// Connect signals
			leftBtnControl!!.buttonDown.connect(basket!!, Basket::onMobileLeftDown)
			leftBtnControl!!.buttonUp.connect(basket!!, Basket::onMobileLeftUp)
			rightBtnControl!!.buttonDown.connect(basket!!, Basket::onMobileRightDown)
			rightBtnControl!!.buttonUp.connect(basket!!, Basket::onMobileRightUp)

			// Apply default opacity
			applyMobileOpacity(defaultMobileOpacity)

			GD.print("Main: Mobile controls bound with opacity ${defaultMobileOpacity}")
		} else {
			GD.print("Main: Mobile controls not found (OK if running on desktop)")
		}
	}

	/**
	 * Applies opacity (alpha/transparency) to both mobile control buttons.
	 *
	 * @param opacity Alpha value from 0.0 (fully transparent) to 1.0 (fully opaque).
	 *                Values outside this range will be clamped.
	 *
	 * Usage examples:
	 * - applyMobileOpacity(0.3)  // 30% visible (subtle)
	 * - applyMobileOpacity(0.5)  // 50% visible (balanced)
	 * - applyMobileOpacity(1.0)  // 100% visible (fully opaque)
	 * - applyMobileOpacity(0.0)  // 0% visible (hidden but still functional)
	 */
	@RegisterFunction
	fun applyMobileOpacity(opacity: Double) {
		val clampedOpacity = opacity.coerceIn(0.0, 1.0)

		leftBtnControl?.modulate = Color(1.0, 1.0, 1.0, clampedOpacity)
		rightBtnControl?.modulate = Color(1.0, 1.0, 1.0, clampedOpacity)

		GD.print("Main: Mobile controls opacity set to $clampedOpacity")
	}

	/**
	 * Shows mobile control buttons by setting opacity to 1.0 (fully opaque).
	 */
	@RegisterFunction
	fun showMobileControls() {
		applyMobileOpacity(1.0)
	}

	/**
	 * Hides mobile control buttons by setting opacity to 0.0 (fully transparent).
	 * Note: Buttons remain functional even when fully transparent.
	 */
	@RegisterFunction
	fun hideMobileControls() {
		applyMobileOpacity(0.0)
	}

	/**
	 * Sets mobile controls to semi-transparent (50% opacity).
	 * Useful for reducing visual clutter while keeping buttons visible.
	 */
	@RegisterFunction
	fun fadeMobileControls() {
		applyMobileOpacity(0.5)
	}

	private fun loadResources() {
		gameOverScene = ResourceLoader.load("res://scenes/game_over_screen.tscn") as? PackedScene
	}

	fun startGameplayLifeCycle() {
		gameplayInstance?.queueFree()
		gameplayInstance = null

		val newGameplay = TimingModeGameplay()
		newGameplay.let {
			it.gameDuration = gameDuration
			it.scoreUpdated.connect(this, Main::onScoreUpdated)
			it.timerUpdated.connect(this, Main::onTimerUpdated)
			it.gameOverSignal.connect(this, Main::onGameOver)

			addChild(it)
			it.connectBasketSignals()
			gameplayInstance = it
		}
	}

	@RegisterFunction
	fun onScoreUpdated(scoreText: String) {
		scoreLabel?.text = scoreText
	}

	@RegisterFunction
	fun onTimerUpdated(timeText: String) {
		timerLabel?.text = timeText
	}

	@RegisterFunction
	fun onGameOver(finalScore: String) {
		showGameOverScreen(finalScore)
	}

	private fun showGameOverScreen(finalScore: String) {
		gameOverLayer?.queueFree()
		val layer = CanvasLayer().apply { this.layer = 100 }
		addChild(layer)
		gameOverLayer = layer

		(gameOverScene?.instantiate() as? GameOverScreen)?.let {
			it.restartRequested.connect(this, Main::onClickRestartBtn)
			layer.addChild(it)
			it.showGameOver(finalScore)
		}
	}

	@RegisterFunction
	fun onClickRestartBtn() {
		try {
			gameplayInstance?.scoreUpdated?.disconnect(this, Main::onScoreUpdated)
			gameplayInstance?.timerUpdated?.disconnect(this, Main::onTimerUpdated)
			gameplayInstance?.gameOverSignal?.disconnect(this, Main::onGameOver)
		} catch (e: Exception) {}

		gameplayInstance?.cleanupGameplay()
		gameplayInstance?.queueFree()
		gameplayInstance = null
		gameOverLayer?.queueFree()
		gameOverLayer = null
		restartPending = true
	}

	private fun restartGamePlayLifeCycle() {
		basket?.resetBasket()
		startGameplayLifeCycle()
	}
}
