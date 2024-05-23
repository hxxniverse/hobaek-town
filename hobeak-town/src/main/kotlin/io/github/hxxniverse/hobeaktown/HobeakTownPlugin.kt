package io.github.hxxniverse.hobeaktown

import io.github.monun.kommand.kommand
import org.bukkit.plugin.java.JavaPlugin

class HobeakTownPlugin : JavaPlugin() {

    companion object {
        lateinit var plugin: JavaPlugin
    }

    override fun onEnable() {
        super.onEnable()
        plugin = this
        println("Hello, Kommand! 9asdasasdasd")

        kommand {
            register("hello") {
                executes {
                    sender.sendMessage("Hello, Kommand!")
                }
            }
        }
    }

    override fun onDisable() {
        super.onDisable()
    }
}