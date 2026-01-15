// THIS FILE IS GENERATED! DO NOT EDIT IT MANUALLY!
package godot.entry

import godot.`annotation`.RegisteredClassMetadata
import godot.api.MultiplayerAPI.RPCMode.DISABLED
import godot.api.MultiplayerPeer.TransferMode.RELIABLE
import godot.core.KtConstructor0
import godot.core.KtRpcConfig
import godot.core.PropertyHint.NONE
import godot.core.VariantParser.NIL
import godot.core.VariantParser.NODE_PATH
import godot.core.VariantParser.STRING
import godot.game.GameOverScreen
import godot.registration.ClassRegistrar
import godot.registration.ClassRegistry
import godot.registration.KtFunctionArgument
import kotlin.Unit
import kotlin.collections.listOf

@RegisteredClassMetadata(
  "GameOverScreen",
  "Control",
  "godot.game.GameOverScreen",
  "src/main/kotlin/godot/game/GameOverScreen.kt",
  "gdj/godot/game/GameOverScreen.gdj",
  "AppleGame",
  "godot.api.Control,godot.api.CanvasItem,godot.api.Node,godot.api.Object,godot.core.KtObject,godot.common.interop.NativeWrapper,godot.common.interop.NativePointer,kotlin.Any",
  "godot.game.GameOverScreen.restartRequested",
  "godot.game.GameOverScreen.scoreLabelPath,godot.game.GameOverScreen.restartBtnPath",
  "godot.game.GameOverScreen._ready,godot.game.GameOverScreen.showGameOver,godot.game.GameOverScreen.hideScreen,godot.game.GameOverScreen.onRestartPressed,godot.game.GameOverScreen.onRestartReleased,godot.game.GameOverScreen.onAnimationComplete,godot.game.GameOverScreen._exitTree,godot.game.GameOverScreen.onClickRestartBtn",
  true,
)
public open class GameOverScreenRegistrar : ClassRegistrar {
  public override fun register(registry: ClassRegistry): Unit {
    with(registry) {
      registerClass<GameOverScreen>(listOf(), GameOverScreen::class, false, "Control", "GameOverScreen", "src/main/kotlin/godot/game/GameOverScreen.kt", "gdj/godot/game/GameOverScreen.gdj") {
        constructor(KtConstructor0(::GameOverScreen))
        notificationFunctions(listOf())
        function(GameOverScreen::_ready, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(GameOverScreen::showGameOver, NIL, STRING, KtFunctionArgument(STRING, "kotlin.String", "scoreText"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(GameOverScreen::hideScreen, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(GameOverScreen::onRestartPressed, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(GameOverScreen::onRestartReleased, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(GameOverScreen::onAnimationComplete, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(GameOverScreen::_exitTree, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(GameOverScreen::onClickRestartBtn, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        signal(GameOverScreen::restartRequested)
        property(GameOverScreen::scoreLabelPath, NODE_PATH, NODE_PATH, "godot.core.NodePath", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
        property(GameOverScreen::restartBtnPath, NODE_PATH, NODE_PATH, "godot.core.NodePath", NONE, "", godot.core.PropertyUsageFlags.NONE.flag)
      }
    }
  }
}
