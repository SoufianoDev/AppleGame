// THIS FILE IS GENERATED! DO NOT EDIT IT MANUALLY!
package godot.entry

import godot.`annotation`.RegisteredClassMetadata
import godot.api.MultiplayerAPI.RPCMode.DISABLED
import godot.api.MultiplayerPeer.TransferMode.RELIABLE
import godot.core.KtConstructor0
import godot.core.KtRpcConfig
import godot.core.VariantCaster.INT
import godot.core.VariantParser.NIL
import godot.core.VariantParser.STRING
import godot.game.ScoreController
import godot.registration.ClassRegistrar
import godot.registration.ClassRegistry
import godot.registration.KtFunctionArgument
import kotlin.Unit
import kotlin.collections.listOf

@RegisteredClassMetadata(
  "ScoreController",
  "Node",
  "godot.game.ScoreController",
  "src/main/kotlin/godot/game/ScoreController.kt",
  "gdj/godot/game/ScoreController.gdj",
  "AppleGame",
  "godot.api.Node,godot.api.Object,godot.core.KtObject,godot.common.interop.NativeWrapper,godot.common.interop.NativePointer,kotlin.Any",
  "",
  "",
  "godot.game.ScoreController._ready,godot.game.ScoreController.addScore,godot.game.ScoreController.setScore,godot.game.ScoreController.getScore,godot.game.ScoreController.getFormattedScore,godot.game.ScoreController.resetScore",
  true,
)
public open class ScoreControllerRegistrar : ClassRegistrar {
  public override fun register(registry: ClassRegistry): Unit {
    with(registry) {
      registerClass<ScoreController>(listOf(), ScoreController::class, false, "Node", "ScoreController", "src/main/kotlin/godot/game/ScoreController.kt", "gdj/godot/game/ScoreController.gdj") {
        constructor(KtConstructor0(::ScoreController))
        notificationFunctions(listOf())
        function(ScoreController::_ready, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(ScoreController::addScore, NIL, INT, KtFunctionArgument(INT, "kotlin.Int", "amount"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(ScoreController::setScore, NIL, INT, KtFunctionArgument(INT, "kotlin.Int", "value"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(ScoreController::getScore, INT, KtFunctionArgument(INT, "kotlin.Int"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(ScoreController::getFormattedScore, STRING, KtFunctionArgument(STRING, "kotlin.String"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(ScoreController::resetScore, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
      }
    }
  }
}
