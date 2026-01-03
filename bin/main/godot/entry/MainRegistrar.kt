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
import godot.game.Main
import godot.registration.ClassRegistrar
import godot.registration.ClassRegistry
import godot.registration.KtFunctionArgument
import kotlin.Unit
import kotlin.collections.listOf

@RegisteredClassMetadata(
  "Main",
  "Node2D",
  "godot.game.Main",
  "src/main/kotlin/godot/game/Main.kt",
  "gdj/godot/game/Main.gdj",
  "AppleGame",
  "godot.api.Node2D,godot.api.CanvasItem,godot.api.Node,godot.api.Object,godot.core.KtObject,godot.common.interop.NativeWrapper,godot.common.interop.NativePointer,kotlin.Any",
  "",
  "godot.game.Main.initialSpawnInterval,godot.game.Main.minSpawnInterval,godot.game.Main.difficultyIncreaseRate,godot.game.Main.gameDuration,godot.game.Main.applesPerWave",
  "godot.game.Main._ready,godot.game.Main._process,godot.game.Main.spawnRandomApple,godot.game.Main.gameOver",
  true,
)
public open class MainRegistrar : ClassRegistrar {
  public override fun register(registry: ClassRegistry): Unit {
    with(registry) {
      registerClass<Main>(listOf(), Main::class, false, "Node2D", "Main", "src/main/kotlin/godot/game/Main.kt", "gdj/godot/game/Main.gdj") {
        constructor(KtConstructor0(::Main))
        notificationFunctions(listOf())
        function(Main::_ready, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(Main::_process, NIL, DOUBLE, KtFunctionArgument(DOUBLE, "kotlin.Double", "delta"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(Main::spawnRandomApple, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(Main::gameOver, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        property(Main::initialSpawnInterval, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Main::minSpawnInterval, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Main::difficultyIncreaseRate, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Main::gameDuration, DOUBLE, DOUBLE, "kotlin.Double", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(Main::applesPerWave, INT, LONG, "kotlin.Int", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
      }
    }
  }
}
