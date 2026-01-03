package godot.game.apples

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.global.GD

@RegisterClass
class GreenApple : BaseApple() {

	@RegisterFunction
	override fun _ready() {
		pointValue = 2
		onReady()
		GD.print("GreenApple ready with $pointValue points")
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		onProcess(delta)
	}
}
