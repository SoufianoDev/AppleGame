package godot.game

interface DisplayManager {
	/** Enters fullscreen mode if supported on current platform */
	fun enterFullscreen()

	/** Exits fullscreen mode if currently fullscreen */
	fun exitFullscreen()

	/** Toggles between fullscreen and windowed mode if supported */
	fun toggleFullscreen()

	/** Checks if fullscreen mode is currently active */
	fun isFullscreen(): Boolean

	/** Checks if fullscreen operations are supported on current platform */
	fun isFullscreenSupported(): Boolean
}
