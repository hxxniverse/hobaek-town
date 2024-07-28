package io.github.hxxniverse.hobeaktown.feature.fish

import FishListener
import io.github.hxxniverse.hobeaktown.feature.fish.entity.Fishes
import io.github.hxxniverse.hobeaktown.feature.fish.entity.FishingRods
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils

class FishFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(Fishes, FishingRods)
        }

        FishCommand().register(plugin)
        Bukkit.getPluginManager().registerEvents(FishListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {
    }
}