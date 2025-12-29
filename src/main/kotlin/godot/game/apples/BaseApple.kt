package godot.game.apples

import godot.annotation.RegisterProperty
import godot.api.RigidBody2D
import godot.core.Vector2
import godot.extension.getNodeAs

abstract class BaseApple : RigidBody2D() {

	@RegisterProperty
	open var pointValue: Int = 1

	open fun onReady() {

//		var i : Int = 0
//		while (i <= getChildCount() ){
//			val child = getChild(i)
//
//			if (child is godot.api.Node2D){
//				child.scale = Vector2(2.0, 2.0)
//			}
//			i++
//		}

	}

	open fun onProcess(delta: Double) {
		if (position.y > 920.0) {
			queueFree()
		}
	}

	fun getPoints(): Int = pointValue
}
