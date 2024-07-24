package io.github.hxxniverse.hobeaktown.feature.bee

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils

class BeeFeature : BaseFeature {
    companion object {
        val BEEHIVE_DURATION_SECOND: Int = 3600
    }

    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(Beehives, BeehiveRewards, BeehiveSetups, BeehiveSetupItems)
        }
        BeeCommand().register(plugin)

        Bukkit.getPluginManager().registerEvents(BeeListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {
    }
}