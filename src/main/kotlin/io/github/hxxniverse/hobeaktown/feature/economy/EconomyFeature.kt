package io.github.hxxniverse.hobeaktown.feature.economy

import io.github.hxxniverse.hobeaktown.feature.economy.entity.Atms
import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoneys
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class EconomyFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(UserMoneys, Atms)
        }
        EconomyConfig.load()
        plugin.server.pluginManager.registerEvents(EconomyListener(), plugin)
        EconomyCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}