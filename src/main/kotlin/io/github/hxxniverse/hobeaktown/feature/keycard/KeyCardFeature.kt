package io.github.hxxniverse.hobeaktown.feature.keycard

import io.github.hxxniverse.hobeaktown.feature.keycard.commands.KeyCardCommand
import io.github.hxxniverse.hobeaktown.feature.keycard.commands.KeycardCommand
import io.github.hxxniverse.hobeaktown.feature.keycard.commands.TagCommand
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.KeyCardDoors
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.KeyCards
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Roles
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.UserKeyCards
import io.github.hxxniverse.hobeaktown.feature.keycard.event.DoorEventListener
import io.github.hxxniverse.hobeaktown.feature.keycard.event.PlayerJoinListener
import io.github.hxxniverse.hobeaktown.feature.keycard.event.TagListener
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class KeyCardFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        plugin.logger.info("[키카드 플러그인] 키카드 역할 플러그인 시작")
        transaction {
            SchemaUtils.create(KeyCards, KeyCardDoors, Roles, UserKeyCards)
        }
        KeyCardCommand().register(plugin)

        plugin.getCommand("키카드")!!.setExecutor(KeycardCommand(databaseManager))
        plugin.getCommand("태그")!!.setExecutor(TagCommand(databaseManager))

        Bukkit.getServer().pluginManager.registerEvents(PlayerJoinListener(databaseManager), plugin)
        Bukkit.getServer().pluginManager.registerEvents(TagListener(databaseManager), plugin)
        Bukkit.getServer().pluginManager.registerEvents(DoorEventListener(plugin, databaseManager), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {
        plugin.logger.info("[후스텔라] 키카드 역할 플러그인 종료")
    }
}

