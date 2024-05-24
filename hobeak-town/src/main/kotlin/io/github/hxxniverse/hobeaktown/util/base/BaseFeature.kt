package io.github.hxxniverse.hobeaktown.util.base

import org.bukkit.plugin.java.JavaPlugin

interface BaseFeature {
    fun enable(plugin: JavaPlugin)
    fun disable(plugin: JavaPlugin)
}

interface BaseCommand {
    fun register(plugin: JavaPlugin)
}