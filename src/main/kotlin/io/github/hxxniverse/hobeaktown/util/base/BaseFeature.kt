package io.github.hxxniverse.hobeaktown.util.base

import org.bukkit.plugin.java.JavaPlugin

interface BaseFeature {
    fun onEnable(plugin: JavaPlugin)
    fun onDisable(plugin: JavaPlugin)
}

interface BaseCommand {
    fun register(plugin: JavaPlugin)
}