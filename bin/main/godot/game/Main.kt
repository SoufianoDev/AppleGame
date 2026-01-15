package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.api.*
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
	
	// Flag for deferred restart
	private var restartPending = false

	@RegisterProperty
	var gameDuration = 60.0

	@RegisterFunction
	override fun _ready() {
		GD.print("Main: Starting game...")
		bindMainUI()
		bindBasket()
		loadResources()
		startGameplayLifeCycle()
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		// Handle deferred restart
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

		// Set initial values
		timerLabel?.text = "Time: ${gameDuration.toInt()}"
		scoreLabel?.text = "Score: 0"
	}

	private fun bindBasket() {
		basket = getNodeOrNull("Basket".asNodePath()) as? Basket
		if (basket != null) {
			GD.print("Main: Basket bound successfully")
		} else {
			GD.printErr("Main: Basket not found in scene tree")
		}
	}

	private fun loadResources() {
		gameOverScene = ResourceLoader.load("res://scenes/game_over_screen.tscn") as? PackedScene
		if (gameOverScene == null) {
			GD.printErr("Main: Failed to load game_over_screen.tscn")
			GD.printErr("Main: Check the file exists at: res://scenes/game_over_screen.tscn")
		} else {
			GD.print("Main: Game over scene loaded successfully")
		}
	}

	fun startGameplayLifeCycle() {
		gameplayInstance?.queueFree()
		gameplayInstance = null

		GD.print("Main: Creating new gameplay instance")
		gameplayInstance = TimingModeGameplay()
		gameplayInstance?.let {
			it.gameDuration = gameDuration

			// Connect signals from gameplay to UI handlers
			it.scoreUpdated.connect(this, Main::onScoreUpdated)
			it.timerUpdated.connect(this, Main::onTimerUpdated)
			it.gameOverSignal.connect(this, Main::onGameOver)

			addChild(it)
			GD.print("Main: Gameplay instance added to scene")
			
			// Connect basket signals after the node is in the scene tree
			it.connectBasketSignals()
			GD.print("Main: Basket signals connection initiated")
		}
	}

	@RegisterFunction
	fun onScoreUpdated(scoreText: String) {
		scoreLabel?.text = scoreText
		GD.print("Main: Score updated to: $scoreText")
	}

	@RegisterFunction
	fun onTimerUpdated(timeText: String) {
		timerLabel?.text = timeText
	}

	@RegisterFunction
	fun onGameOver(finalScore: String) {
		GD.print("Main: Game over! Final score: $finalScore")
		showGameOverScreen(finalScore)
		gameplayInstance?.setProcess(false)
		gameplayInstance?.setPhysicsProcess(false)
	}

	private fun showGameOverScreen(finalScore: String) {
		// Clean up previous instances
		gameOverLayer?.queueFree()
		gameOverLayer = null
		gameOverScreen = null

		if (gameOverScene == null) {
			GD.printErr("Main: gameOverScene is null! Cannot show game over screen")
			return
		}

		// Create CanvasLayer for proper overlay rendering
		val layer = CanvasLayer()
		layer.layer = 100 // Render above all other UI layers
		addChild(layer)
		gameOverLayer = layer
		GD.print("Main: CanvasLayer created for Game Over screen (layer=100)")

		// Instantiate GameOverScreen
		gameOverScreen = gameOverScene?.instantiate() as? GameOverScreen
		if (gameOverScreen == null) {
			GD.printErr("Main: Failed to instantiate GameOverScreen")
			return
		}

		gameOverScreen?.let {
			// Connect restart signal
			it.restartRequested.connect(this, Main::onClickRestartBtn)

			// Add to CanvasLayer (not directly to Main)
			layer.addChild(it)
			it.showGameOver(finalScore)
			GD.print("Main: Game over screen shown on CanvasLayer with score: $finalScore")
		}
	}

	@RegisterFunction
	fun onClickRestartBtn() {
		GD.print("Main: Restart button clicked - initiating cleanup sequence")
		
		// Step 1: Validate instance exists
		if (gameplayInstance == null) {
			GD.printErr("Main: Cannot restart - gameplay instance already null")
			// Still need to clean up UI
			gameOverLayer?.queueFree()
			gameOverLayer = null
			gameOverScreen = null
			return
		}
		
		// Step 2: Disconnect all signals from gameplay instance
		try {
			gameplayInstance?.scoreUpdated?.disconnect(this, Main::onScoreUpdated)
			gameplayInstance?.timerUpdated?.disconnect(this, Main::onTimerUpdated)
			gameplayInstance?.gameOverSignal?.disconnect(this, Main::onGameOver)
			GD.print("Main: Signals disconnected from gameplay instance")
		} catch (e: Exception) {
			GD.printErr("Main: Error disconnecting signals: ${e.message}")
		}
		
		// Step 3: Call explicit cleanup on gameplay instance
		try {
			gameplayInstance?.cleanupGameplay()
			GD.print("Main: Gameplay cleanup method called")
		} catch (e: Exception) {
			GD.printErr("Main: Error during cleanup: ${e.message}")
		}
		
		// Step 4: Free gameplay node
		gameplayInstance?.queueFree()
		gameplayInstance = null
		GD.print("Main: Gameplay instance queued for freeing")
		
		// Step 5: Free game over UI
		gameOverLayer?.queueFree()
		gameOverLayer = null
		gameOverScreen = null
		GD.print("Main: Game Over UI queued for freeing")
		
		// Step 6: Set restart flag for next frame
		// This ensures queueFree() completes before creating new instance
		restartPending = true
		GD.print("Main: Restart flag set - will restart on next frame")
	}

	/**
	 * Restarts the gameplay lifecycle.
	 * Called from _process() when restart flag is set.
	 */
	private fun restartGamePlayLifeCycle() {
		GD.print("Main: Restarting game...")
		
		// Reset basket to empty state (basket_0)
		basket?.resetBasket()
		
		startGameplayLifeCycle()
	}
}
