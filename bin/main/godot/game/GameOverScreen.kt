package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterProperty
import godot.annotation.RegisterSignal
import godot.api.*
import godot.core.asNodePath
import godot.core.signal0
import godot.global.GD

@RegisterClass
class GameOverScreen : Control() {

	@RegisterSignal
	val restartRequested by signal0()

	private lateinit var scoreLabel: Label
	private lateinit var restartBtn: TextureButton
	
	// Animation state management to prevent double-clicks
	private var isAnimating = false
	private var buttonTween: Tween? = null

	@RegisterProperty
	var scoreLabelPath = "BackgroundDim/CenterContainer/ContentRoot/ScorePannelRoot/ScoreNumber".asNodePath()

	@RegisterProperty
	var restartBtnPath = "BackgroundDim/CenterContainer/ContentRoot/ScorePannelRoot/RestartButton".asNodePath()

	@RegisterFunction
	override fun _ready() {
		hide()
		bindNodes()
	}

	private fun bindNodes() {
		val scoreNode = getNodeOrNull(scoreLabelPath)
		val restartNode = getNodeOrNull(restartBtnPath)

		if (scoreNode is Label) {
			scoreLabel = scoreNode
			GD.print("GameOverScreen: score label bound")
		} else {
			GD.printErr("GameOverScreen: score label not found at $scoreLabelPath")
		}

		if (restartNode is TextureButton) {
			restartBtn = restartNode
			// Connect button_down and button_up signals for animation
			restartBtn.buttonDown.connect(this, GameOverScreen::onRestartPressed)
			restartBtn.buttonUp.connect(this, GameOverScreen::onRestartReleased)
			GD.print("GameOverScreen: restart button bound with animation signals")
		} else {
			GD.printErr("GameOverScreen: restart button not found at $restartBtnPath")
		}
	}


	@RegisterFunction
	fun showGameOver(scoreText: String) {
		if (!::scoreLabel.isInitialized) {
			GD.printErr("GameOverScreen: showGameOver() called before initialization")
			return
		}

		scoreLabel.text = scoreText
		show()
		GD.print("GameOverScreen: Showing with score: $scoreText")
	}

	@RegisterFunction
	fun hideScreen() {
		// Kill any active animation before hiding
		buttonTween?.kill()
		buttonTween = null
		isAnimating = false
		hide()
	}

	/**
	 * Called when restart button is pressed down.
	 * Applies BUTTON_PRESS animation style for visual feedback.
	 */
	@RegisterFunction
	fun onRestartPressed() {
		if (isAnimating) {
			GD.print("GameOverScreen: Button already animating, ignoring press")
			return
		}
		
		isAnimating = true
		GD.print("GameOverScreen: Restart button pressed - applying animation")
		
		// Apply BUTTON_PRESS animation style
		buttonTween = AnimationStyle.BUTTON_PRESS.applyToNode(restartBtn)
	}

	/**
	 * Called when restart button is released.
	 * Applies BUTTON_RELEASE animation, then emits restart signal after animation completes.
	 */
	@RegisterFunction
	fun onRestartReleased() {
		if (!isAnimating) {
			GD.print("GameOverScreen: Button not in pressed state, ignoring release")
			return
		}
		
		GD.print("GameOverScreen: Restart button released - applying animation")
		
		// Kill any existing tween before creating new one
		buttonTween?.kill()
		
		// Apply BUTTON_RELEASE animation style
		buttonTween = AnimationStyle.BUTTON_RELEASE.applyToNode(restartBtn)
		
		// Connect to tween finished signal to emit restart after animation
		buttonTween?.finished?.connect(this, GameOverScreen::onAnimationComplete)
	}

	/**
	 * Called when button animation completes.
	 * Emits restart signal and hides the screen.
	 */
	@RegisterFunction
	fun onAnimationComplete() {
		isAnimating = false
		GD.print("GameOverScreen: Animation complete - emitting restart signal")
		restartRequested.emit()
		hideScreen()
	}

	/**
	 * Cleanup method to be called on node exit.
	 * Ensures tween is properly killed to prevent memory leaks.
	 */
	@RegisterFunction
	override fun _exitTree() {
		// Don't call super._exitTree() as it's not implemented in godot-kotlin-jvm
		buttonTween?.kill()
		buttonTween = null
		isAnimating = false
		GD.print("GameOverScreen: Cleanup complete")
	}

	// Deprecated method - kept for compatibility but no longer called
	@RegisterFunction
	fun onClickRestartBtn() {
		GD.print("GameOverScreen: Restart button clicked (deprecated method)")
		restartRequested.emit()
		hideScreen()
	}
}
