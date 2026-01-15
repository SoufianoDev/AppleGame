package godot.game

import godot.api.Node
import godot.api.Tween
import godot.core.Vector2
import godot.global.GD

/**
 * Defines reusable button animation styles with configurable parameters.
 * Each style encapsulates scale factor, duration, and easing functions for consistent UI feedback.
 * 
 * Note: Tween TransitionType and EaseType enums are not accessible in godot-kotlin-jvm 0.13.1,
 * so we use a simplified approach with just scale and duration parameters.
 */
enum class AnimationStyle(
	val scaleFactor: Float,
	val durationSeconds: Float
) {
	/**
	 * Standard button press animation - scales down slightly for tactile feedback.
	 * Use: When button is pressed (button_down signal)
	 */
	BUTTON_PRESS(
		scaleFactor = 0.92f,
		durationSeconds = 0.08f
	),

	/**
	 * Button release animation with bounce for satisfying feedback.
	 * Use: When button is released (button_up signal)
	 */
	BUTTON_RELEASE(
		scaleFactor = 1.0f,
		durationSeconds = 0.12f
	),

	/**
	 * Subtle hover emphasis animation (optional, for future use).
	 * Use: When mouse enters button area (mouse_entered signal)
	 */
	BUTTON_HOVER(
		scaleFactor = 1.05f,
		durationSeconds = 0.15f
	);

	/**
	 * Creates and configures a Tween animation for the target node.
	 *
	 * @param targetNode The node to animate (must be in scene tree)
	 * @param propertyPath The property to animate (e.g., "scale" for Node2D/Control)
	 * @return Configured Tween instance ready to start, or null if creation fails
	 */
	fun applyToNode(targetNode: Node, propertyPath: String = "scale"): Tween? {
		// Create tween instance attached to the target node's tree
		val tween = targetNode.createTween()
		if (tween == null) {
			GD.printErr("AnimationStyle: Failed to create Tween for ${targetNode.name}")
			return null
		}

		// Configure tween animation target
		val targetScale = Vector2(scaleFactor.toDouble(), scaleFactor.toDouble())
		
		// Animate the specified property
		tween.tweenProperty(
			targetNode,
			propertyPath,
			targetScale,
			durationSeconds.toDouble()
		)

		return tween
	}
}
