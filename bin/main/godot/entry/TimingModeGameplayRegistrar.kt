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
import godot.core.VariantParser.STRING
import godot.game.TimingModeGameplay
import godot.registration.ClassRegistrar
import godot.registration.ClassRegistry
import godot.registration.KtFunctionArgument
import kotlin.Unit
import kotlin.collections.listOf

@RegisteredClassMetadata(
  "TimingModeGameplay",
  "Node2D",
  "godot.game.TimingModeGameplay",
  "src/main/kotlin/godot/game/TimingModeGamePlay.kt",
  "gdj/godot/game/TimingModeGameplay.gdj",
  "AppleGame",
  "godot.api.Node2D,godot.api.CanvasItem,godot.api.Node,godot.api.Object,godot.core.KtObject,godot.common.interop.NativeWrapper,godot.common.interop.NativePointer,kotlin.Any",
  "godot.game.TimingModeGameplay.scoreUpdated,godot.game.TimingModeGameplay.timerUpdated,godot.game.TimingModeGameplay.gameOverSignal",
  "godot.game.TimingModeGameplay.initialSpawnInterval,godot.game.TimingModeGameplay.minSpawnInterval,godot.game.TimingModeGameplay.difficultyIncreaseRate,godot.game.TimingModeGameplay.gameDuration,godot.game.TimingModeGameplay.applesPerWave",
  "godot.game.TimingModeGameplay._ready,godot.game.TimingModeGameplay.connectBasketSignals,godot.game.TimingModeGameplay._process,godot.game.TimingModeGameplay.onAppleCollected,godot.game.TimingModeGameplay.cleanupGameplay",
  true,
)
public open class TimingModeGameplayRegistrar : ClassRegistrar {
  public override fun register(registry: ClassRegistry): Unit {
    with(registry) {
      registerClass<TimingModeGameplay>(listOf(), TimingModeGameplay::class, false, "Node2D", "TimingModeGameplay", "src/main/kotlin/godot/game/TimingModeGamePlay.kt", "gdj/godot/game/TimingModeGameplay.gdj") {
        constructor(KtConstructor0(::TimingModeGameplay))
        notificationFunctions(listOf())
        function(TimingModeGameplay::_ready, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(TimingModeGameplay::connectBasketSignals, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(TimingModeGameplay::_process, NIL, DOUBLE, KtFunctionArgument(DOUBLE, "kotlin.Double", "delta"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(TimingModeGameplay::onAppleCollected, NIL, INT, KtFunctionArgument(INT, "kotlin.Int", "points"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(TimingModeGameplay::cleanupGameplay, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        signal(TimingModeGameplay::scoreUpdated, KtFunctionArgument(STRING, "kotlin.String", "p0"))
        signal(TimingModeGameplay::timerUpdated, KtFunctionArgument(STRING, "kotlin.String", "p0"))
        signal(TimingModeGameplay::gameOverSignal, KtFunctionArgument(STRING, "kotlin.String", "p0"))
        property(TimingModeGameplay::initialSpawnInterval, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(TimingModeGameplay::minSpawnInterval, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(TimingModeGameplay::difficultyIncreaseRate, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(TimingModeGameplay::gameDuration, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(TimingModeGameplay::applesPerWave, INT, LONG, "kotlin.Int", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
      }
    }
  }
}
