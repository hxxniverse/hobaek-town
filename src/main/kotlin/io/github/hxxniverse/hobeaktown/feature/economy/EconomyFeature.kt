package io.github.hxxniverse.hobeaktown.feature.economy

import io.github.hxxniverse.hobeaktown.feature.economy.entity.Atms
import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoneys
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class EconomyFeature : BaseFeature {
    override fun enable(plugin: JavaPlugin) {
        transaction {
            SchemaUtils.drop(UserMoneys, Atms)
            SchemaUtils.create(UserMoneys, Atms)
        }
        EconomyConfig.load()
        plugin.server.pluginManager.registerEvents(EconomyListener(), plugin)
        EconomyCommand().register(plugin)
    }

    override fun disable(plugin: JavaPlugin) {

    }
}