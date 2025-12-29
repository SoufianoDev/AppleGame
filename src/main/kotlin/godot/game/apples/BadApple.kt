package godot.game.apples

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.global.GD

@RegisterClass
class BadApple : BaseApple() {

	@RegisterFunction
	override fun _ready() {
		pointValue = -1
		onReady()
		GD.print("BadApple ready with $pointValue points")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		onProcess(delta)
	}
}
