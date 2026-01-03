// THIS FILE IS GENERATED! DO NOT EDIT IT MANUALLY!
package godot.entry.opDfRZOZLBjJznqBvFrH

import godot.entry.BadAppleRegistrar
import godot.entry.BaseAppleRegistrar
import godot.entry.BasketRegistrar
import godot.entry.GreenAppleRegistrar
import godot.entry.MainRegistrar
import godot.entry.RedAppleRegistrar
import godot.game.Basket
import godot.game.Main
import godot.game.apples.BadApple
import godot.game.apples.BaseApple
import godot.game.apples.GreenApple
import godot.game.apples.RedApple
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
  public override val classRegistrarCount: Int = 6

  public override val projectName: String = "AppleGame"

  public override fun Context.`init`(): Unit {
    BasketRegistrar().register(registry)
    MainRegistrar().register(registry)
    BadAppleRegistrar().register(registry)
    BaseAppleRegistrar().register(registry)
    GreenAppleRegistrar().register(registry)
    RedAppleRegistrar().register(registry)
  }

  public override fun Context.initEngineTypes(): Unit {
    registerVariantMapping()
    registerEngineTypes()
    registerEngineTypeMethods()
  }

  public override fun Context.getRegisteredClasses(): List<KClass<*>> = listOf(Basket::class,
      Main::class, BadApple::class, BaseApple::class, GreenApple::class, RedApple::class)
}
