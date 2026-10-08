// THIS FILE IS GENERATED! DO NOT EDIT IT MANUALLY!
package godot.entry

import godot.`annotation`.RegisteredClassMetadata
import godot.api.MultiplayerAPI.RPCMode.DISABLED
import godot.api.MultiplayerPeer.TransferMode.RELIABLE
import godot.core.KtConstructor0
import godot.core.KtRpcConfig
import godot.core.VariantParser.NIL
import godot.core.VariantParser.OBJECT
import godot.game.desktop.DesktopDisplayManager
import godot.registration.ClassRegistrar
import godot.registration.ClassRegistry
import godot.registration.KtFunctionArgument
import kotlin.Unit
import kotlin.collections.listOf

@RegisteredClassMetadata(
  "DesktopDisplayManager",
  "Control",
  "godot.game.desktop.DesktopDisplayManager",
  "src/main/kotlin/godot/game/desktop/DesktopDisplayManager.kt",
  "gdj/godot/game/desktop/DesktopDisplayManager.gdj",
  "AppleGame",
  "godot.api.Control,godot.game.DisplayManager,godot.api.CanvasItem,godot.api.Node,godot.api.Object,godot.core.KtObject,godot.common.interop.NativeWrapper,godot.common.interop.NativePointer,kotlin.Any",
  "",
  "",
  "godot.game.desktop.DesktopDisplayManager._ready,godot.game.desktop.DesktopDisplayManager._input",
  true,
)
public open class DesktopDisplayManagerRegistrar : ClassRegistrar {
  public override fun register(registry: ClassRegistry): Unit {
    with(registry) {
      registerClass<DesktopDisplayManager>(listOf(), DesktopDisplayManager::class, false, "Control", "DesktopDisplayManager", "src/main/kotlin/godot/game/desktop/DesktopDisplayManager.kt", "gdj/godot/game/desktop/DesktopDisplayManager.gdj") {
        constructor(KtConstructor0(::DesktopDisplayManager))
        notificationFunctions(listOf())
        function(DesktopDisplayManager::_ready, NIL, KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
        function(DesktopDisplayManager::_input, NIL, OBJECT, KtFunctionArgument(OBJECT, "godot.api.InputEvent", "event"), KtFunctionArgument(NIL, "kotlin.Unit"), KtRpcConfig(DISABLED.id.toInt(), false, RELIABLE.id.toInt(), 0))
      }
    }
  }
}
