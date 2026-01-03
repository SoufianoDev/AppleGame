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
import godot.core.VariantParser.OBJECT
import godot.game.Basket
import godot.registration.ClassRegistrar
import godot.registration.ClassRegistry
import godot.registration.KtFunctionArgument
import kotlin.Unit
import kotlin.collections.listOf

@RegisteredClassMetadata(
  "Basket",
  "Area2D",
  "godot.game.Basket",
  "src/main/kotlin/godot/game/Basket.kt",
  "gdj/godot/game/Basket.gdj",
  "AppleGame",
  "godot.api.Area2D,godot.api.CollisionObject2D,godot.api.Node2D,godot.api.CanvasItem,godot.api.Node,godot.api.Object,godot.core.KtObject,godot.common.interop.NativeWrapper,godot.common.interop.NativePointer,kotlin.Any",
  "",
  "godot.game.Basket.speed,godot.game.Basket.score,godot.game.Basket.leanAmount,godot.game.Basket.leanSpeed,godot.game.Basket.squashAmount,godot.game.Basket.squashDuration,godot.game.Basket.stretchAmount,godot.game.Basket.stretchDuration,godot.game.Basket.settleDuration",
  "godot.game.Basket._ready,godot.game.Basket._process,godot.game.Basket.onBodyEntered",
  true,
)
public open class BasketRegistrar : ClassRegistrar {
  public override fun register(registry: ClassRegistry): Unit {
    with(registry) {
      registerClass<Basket>(listOf(), Basket::class, false, "Area2D", "Basket", "src/main/kotlin/godot/game/Basket.kt", "gdj/godot/game/Basket.gdj") {
        constructor(KtConstructor0(::Basket))
        notificationFunctions(listOf())
        function(Basket::_ready, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(Basket::_process, NIL, DOUBLE, KtFunctionArgument(DOUBLE, "kotlin.Double", "delta"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(Basket::onBodyEntered, NIL, OBJECT, KtFunctionArgument(OBJECT, "godot.api.Node", "body"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        property(Basket::speed, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Basket::score, INT, LONG, "kotlin.Int", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Basket::leanAmount, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Basket::leanSpeed, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Basket::squashAmount, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Basket::squashDuration, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Basket::stretchAmount, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Basket::stretchDuration, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Basket::settleDuration, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
      }
    }
  }
}
