package io.github.hxxniverse.hobeaktown.feature.keycard

import io.github.hxxniverse.hobeaktown.feature.keycard.commands.KeyCardCommand
import io.github.hxxniverse.hobeaktown.feature.keycard.commands.TagCommand
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.*
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class KeyCardFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        transaction {
            SchemaUtils.create(KeyCards, KeyCardDoors, Roles, UserKeyCards)
            Role.initialize()
        }
        KeyCardCommand().register(plugin)
        TagCommand().register(plugin)

        plugin.server.pluginManager.registerEvents(KeyCardListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {
    }
}

