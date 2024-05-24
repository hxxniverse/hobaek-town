package io.github.hxxniverse.hobeaktown.feature.economy

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoneys
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils

class EconomyFeature : BaseFeature {
    override fun enable(plugin: JavaPlugin) {
        SchemaUtils.create(UserMoneys)
        EconomyConfig.load()
        plugin.server.pluginManager.registerEvents(EconomyListener(), plugin)
        EconomyCommand().register(plugin)
    }

    override fun disable(plugin: JavaPlugin) {

    }
}