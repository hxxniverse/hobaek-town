package io.github.hxxniverse.hobeaktown.feature.police

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin

class PoliceFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        PoliceCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}