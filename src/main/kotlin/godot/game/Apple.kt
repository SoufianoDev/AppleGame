package godot.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.RigidBody2D

@RegisterClass
class Apple : RigidBody2D() {

	@RegisterFunction
	override fun _process(delta: Double) {
		if (position.y > 900.0) {
			queueFree()
		}
	}
}
