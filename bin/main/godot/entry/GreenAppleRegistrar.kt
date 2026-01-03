// THIS FILE IS GENERATED! DO NOT EDIT IT MANUALLY!
package godot.entry

import godot.`annotation`.RegisteredClassMetadata
import godot.api.MultiplayerAPI.RPCMode.DISABLED
import godot.api.MultiplayerPeer.TransferMode.RELIABLE
import godot.core.KtConstructor0
import godot.core.KtRpcConfig
import godot.core.PropertyHint.NONE
import godot.core.VariantCaster.INT
import godot.core.VariantParser.DOUBLE
import godot.core.VariantParser.LONG
import godot.core.VariantParser.NIL
import godot.game.apples.GreenApple
import godot.registration.ClassRegistrar
import godot.registration.ClassRegistry
import godot.registration.KtFunctionArgument
import kotlin.Unit
import kotlin.collections.listOf

@RegisteredClassMetadata(
  "GreenApple",
  "RigidBody2D",
  "godot.game.apples.GreenApple",
  "src/main/kotlin/godot/game/apples/GreenApple.kt",
  "gdj/godot/game/apples/GreenApple.gdj",
  "AppleGame",
  "godot.game.apples.BaseApple,godot.api.RigidBody2D,godot.api.PhysicsBody2D,godot.api.CollisionObject2D,godot.api.Node2D,godot.api.CanvasItem,godot.api.Node,godot.api.Object,godot.core.KtObject,godot.common.interop.NativeWrapper,godot.common.interop.NativePointer,kotlin.Any",
  "",
  "godot.game.apples.GreenApple.pointValue",
  "godot.game.apples.GreenApple._ready,godot.game.apples.GreenApple._process",
  true,
)
public open class GreenAppleRegistrar : ClassRegistrar {
  public override fun register(registry: ClassRegistry): Unit {
    with(registry) {
      registerClass<GreenApple>(listOf(), GreenApple::class, false, "RigidBody2D", "GreenApple", "src/main/kotlin/godot/game/apples/GreenApple.kt", "gdj/godot/game/apples/GreenApple.gdj") {
        constructor(KtConstructor0(::GreenApple))
        notificationFunctions(listOf())
        function(GreenApple::_ready, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(GreenApple::_process, NIL, DOUBLE, KtFunctionArgument(DOUBLE, "kotlin.Double", "delta"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        property(GreenApple::pointValue, INT, LONG, "kotlin.Int", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
      }
    }
  }
}
