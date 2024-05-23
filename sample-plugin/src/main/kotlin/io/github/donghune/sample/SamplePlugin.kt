package io.github.donghune.sample

import io.github.monun.kommand.kommand
import org.bukkit.plugin.java.JavaPlugin

class SamplePlugin: JavaPlugin() {
    override fun onEnable() {
        super.onEnable()

        println("Hello, Kommand! 9asdasasdasd")

        kommand {
            register("hello") {
                requires { playerOrNull != null }
                executes {
                    player.sendMessage("Hello, ${player.name}")
                }
            }
        }
    }

    override fun onDisable() {
        super.onDisable()
    }
}