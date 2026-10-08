package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.*
import godot.core.Color
import godot.core.Vector2
import godot.core.asNodePath
import godot.game.desktop.DesktopDisplayManager
import godot.global.GD
import java.io.File

@RegisterClass
class Main : Control() {

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

	// Display manager for desktop fullscreen control
	private var displayManager: DisplayManager? = null

	private var restartPending = false
	private var background : TextureRect? = null

	@RegisterProperty
	var gameDuration = 60.0

	/**
	 * Default opacity for mobile control buttons (0.0 = fully transparent, 1.0 = fully opaque).
	 * This value is applied when buttons are first bound in _ready().
	 * Can be modified in Godot Inspector.
	 */
	@RegisterProperty
	var defaultMobileOpacity = 0.3

	@RegisterFunction
	override fun _ready() {
		GD.print("\n ========================")
		detectDesktopEnv()
		GD.print("\n ========================")
		GD.print("\nMain: Starting game...")
		bindMainUI()
		bindBasket()
		bindMobileControls()
		loadResources()
		initializeDisplayManager()
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

		detectMobilesEnv()

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
	 * Initializes the display manager for desktop environments.
	 * Only creates DesktopDisplayManager when running on desktop platforms.
	 */
	private fun initializeDisplayManager() {
		if (envIsDesktop()) {
			val manager = DesktopDisplayManager()
			addChild(manager)
			displayManager = manager
			GD.print("MaFilein: DesktopDisplayManager initialized")

			// Optional: Auto-enter fullscreen on desktop startup
			// manager.enterFullscreen()
		} else {
			GD.print("Main: Skipping DisplayManager on non-desktop platform")
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
		leftBtnControl?.visible = true
		rightBtnControl?.visible = true
	}

	/**
	 * Hides mobile control buttons by setting opacity to 0.0 (fully transparent).
	 * Note: Buttons remain functional even when fully transparent.
	 */
	@RegisterFunction
	fun hideMobileControls() {
		leftBtnControl?.visible = false
		rightBtnControl?.visible = false
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

	@RegisterFunction
	fun onWindowSizeChanged() {

		background = getNodeOrNull("TextureRect".asNodePath()) as? TextureRect

		getTree()?.root?.sizeChanged?.connect(this, Main::onWindowSizeChanged)

		background?.let { bg ->
			val texture = bg.texture
			val windowSize = getViewport()?.getVisibleRect()?.size ?: Vector2(1920, 1080)

			texture?.let { tex ->
				val texSize = tex.getSize()

				val scaleX = windowSize.x / texSize.x
				val scaleY = windowSize.y / texSize.y

				val finalScale = if (scaleX > scaleY) scaleX else scaleY

				bg.setScale(Vector2(finalScale, finalScale))
				bg.setPivotOffset(texSize / 2.0)
				bg.setPosition(windowSize / 2.0 - (texSize * finalScale / 2.0))
			}
		}
	}

	private fun restartGamePlayLifeCycle() {
		basket?.resetBasket()
		startGameplayLifeCycle()
	}

	// ================= PLATFORM DETECTION =================

	private fun detectOsEnv(): String {
		val osName = OS.getName()
		return osName.lowercase()
	}

	private fun printOsEnvName() {
		GD.print("Main: OS name: ${detectOsEnv()}")
	}

	private fun envIsMobile(): Boolean {
		return OS.hasFeature("mobile") ||
				OS.hasFeature("android") ||
				OS.hasFeature("ios")
	}

	private fun envIsDesktop(): Boolean {
		return OS.hasFeature("pc") ||
				OS.hasFeature("Windows") ||
				OS.hasFeature("macOS") ||
				OS.hasFeature("Linux")
	}

	private fun detectMobilesEnv() {
		if (envIsMobile())
			showMobileControls()
		else
			hideMobileControls()
	}

	private fun detectDesktopEnv() {
		if (envIsDesktop()) {
			if (detectOsEnv() == "linux") {
				val distro = getLinuxDistro()
				GD.print("Os : $distro")
			}
		}
	}

	// Cache the distro result to avoid file reading on every call
	private var linuxDistroCache: String? = null

	fun getLinuxDistro(): String {
		linuxDistroCache?.let { return it }

		val osRelease = File("/etc/os-release")
		if (osRelease.exists()) {
			try {
				val lines = osRelease.readLines()
				val idLine = lines.find { it.startsWith("ID=") }
				val distroName = idLine
					?.substringAfter("=")
					?.removeSurrounding("\"")
					?: "Unknown Linux"

				val result = "Linux ($distroName)"
				linuxDistroCache = result
				return result
			} catch (e: Exception) {
				return "Linux (Unknown/Error)"
			}
		}
		return "Unknown Linux"
	}
}
