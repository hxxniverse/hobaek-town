package io.github.hxxniverse.hobeaktown.feature.user

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class UserFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        Bukkit.getPluginManager().registerEvents(UserListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

