package io.github.hxxniverse.hobeaktown.feature.keycard

import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.Bed
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.Beds
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.FatigueAreas
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.UserFatigues
import io.github.hxxniverse.hobeaktown.feature.keycard.commands.KeyCardCommand
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.KeyCard
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.KeyCardDoors
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.KeyCards
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class KeyCardFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(KeyCards, KeyCardDoors)
            Bed.initial()
        }
        KeyCardCommand().register(plugin)

        plugin.server.pluginManager.registerEvents(KeyCardListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {}
}

