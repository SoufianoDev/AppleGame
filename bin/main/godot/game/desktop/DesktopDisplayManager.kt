package godot.game.desktop

import godot.game.DisplayManager

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Control
import godot.api.DisplayServer
import godot.api.InputEvent
import godot.api.InputEventKey
import godot.api.OS
import godot.core.Key
import godot.global.GD

@RegisterClass
class DesktopDisplayManager : Control(), DisplayManager {

	private var isFullscreenMode = false

	@RegisterFunction
	override fun _ready() {
		// Auto-disable on non-desktop platforms as safety measure
		if (!isRunningOnDesktop()) {
			GD.print("DesktopDisplayManager: Not on desktop, disabling.")
			queueFree()
			return
		}

		GD.print("DesktopDisplayManager: Initialized for desktop.")
	}

	@RegisterFunction
	override fun _input(event: InputEvent?) {

		if (event is InputEventKey && event.isPressed()&& !event.isEcho()) {
			// F key toggles fullscreen
			if (event.keycode == Key.F) {
				toggleFullscreen()
			}
			// Escape exits fullscreen (but doesn't re-enter)
			else if (event.keycode == Key.ESCAPE && isFullscreen()) {
				exitFullscreen()
			}
		}
	}

	override fun enterFullscreen() {
		if (!isFullscreenSupported()) return
		DisplayServer.windowSetMode(DisplayServer.WindowMode.FULLSCREEN,0)
		isFullscreenMode = true
		GD.print("Entered fullscreen mode")
	}

	override fun exitFullscreen() {
		if (isFullscreen()) {
			DisplayServer.windowSetMode(DisplayServer.WindowMode.WINDOWED,0)
			isFullscreenMode = false
			GD.print("Exited fullscreen mode")
		}
	}

	override fun toggleFullscreen() {
		if (!isFullscreenSupported()) return
		if (isFullscreen()) {
			exitFullscreen()
		} else {
			enterFullscreen()
		}
	}

	override fun isFullscreen(): Boolean {
		return DisplayServer.windowGetMode() == DisplayServer.WindowMode.FULLSCREEN
	}

	override fun isFullscreenSupported(): Boolean {
		// Fullscreen is typically supported on desktop platforms
		return isRunningOnDesktop()
	}

	/** Utilizes your existing platform detection */
	private fun isRunningOnDesktop(): Boolean {
		val osName = OS.getName()?.lowercase() ?: return false
		return osName.contains("windows") ||
				osName.contains("macos") ||
				osName.contains("linux") ||
				osName.contains("bsd") ||
				OS.hasFeature("pc") ||
				OS.hasFeature("desktop")
	}
}
