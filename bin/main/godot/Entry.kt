// THIS FILE IS GENERATED! DO NOT EDIT IT MANUALLY!
package godot.entry.opDfRZOZLBjJznqBvFrH

import godot.entry.BadAppleRegistrar
import godot.entry.BaseAppleRegistrar
import godot.entry.BasketRegistrar
import godot.entry.DesktopDisplayManagerRegistrar
import godot.entry.GameOverScreenRegistrar
import godot.entry.GreenAppleRegistrar
import godot.entry.MainRegistrar
import godot.entry.RedAppleRegistrar
import godot.entry.ScoreControllerRegistrar
import godot.entry.TimingModeGameplayRegistrar
import godot.game.Basket
import godot.game.GameOverScreen
import godot.game.Main
import godot.game.ScoreController
import godot.game.TimingModeGameplay
import godot.game.apples.BadApple
import godot.game.apples.BaseApple
import godot.game.apples.GreenApple
import godot.game.apples.RedApple
import godot.game.desktop.DesktopDisplayManager
import godot.registerEngineTypeMethods
import godot.registerEngineTypes
import godot.registerVariantMapping
import godot.registration.Entry
import godot.registration.Entry.Context
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.listOf
import kotlin.reflect.KClass

public class Entry : Entry() {
  public override val classRegistrarCount: Int = 10

  public override val projectName: String = "AppleGame"

  public override fun Context.`init`(): Unit {
    BasketRegistrar().register(registry)
    GameOverScreenRegistrar().register(registry)
    MainRegistrar().register(registry)
    ScoreControllerRegistrar().register(registry)
    TimingModeGameplayRegistrar().register(registry)
    BadAppleRegistrar().register(registry)
    BaseAppleRegistrar().register(registry)
    GreenAppleRegistrar().register(registry)
    RedAppleRegistrar().register(registry)
    DesktopDisplayManagerRegistrar().register(registry)
  }

  public override fun Context.initEngineTypes(): Unit {
    registerVariantMapping()
    registerEngineTypes()
    registerEngineTypeMethods()
  }

  public override fun Context.getRegisteredClasses(): List<KClass<*>> = listOf(Basket::class,
      GameOverScreen::class, Main::class, ScoreController::class, TimingModeGameplay::class,
      BadApple::class, BaseApple::class, GreenApple::class, RedApple::class,
      DesktopDisplayManager::class)
}
