package io.github.hxxniverse.hobeaktown.feature.fatigue

import io.github.hxxniverse.hobeaktown.feature.fatigue.commands.BedCommand
import io.github.hxxniverse.hobeaktown.feature.fatigue.commands.FatigueCommand
import io.github.hxxniverse.hobeaktown.feature.fatigue.config.AreaFatigueConfig
import io.github.hxxniverse.hobeaktown.feature.fatigue.config.UserFatigueConfig
import io.github.hxxniverse.hobeaktown.feature.fatigue.config.UserItemFatigueData
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.*
import io.github.hxxniverse.hobeaktown.feature.fatigue.scheduler.AreaFatigueScheduler
import io.github.hxxniverse.hobeaktown.feature.fatigue.scheduler.BedScheduler
import io.github.hxxniverse.hobeaktown.feature.fatigue.scheduler.ItemFatigueScheduler
import io.github.hxxniverse.hobeaktown.feature.fatigue.scheduler.WalkFatigueScheduler
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class FatigueFeature : BaseFeature {

    val playerZones = mutableMapOf<Player, FatigueArea?>()
    val lastChangeAreaTimes = mutableMapOf<Player, Long>()
    val walkingTimes = mutableMapOf<Player, Long>()
    val playerBedTime = mutableMapOf<Player, Pair<String, Long>>()
    val playerFatigueItem = mutableMapOf<Player, MutableList<UserItemFatigueData>>()

    override fun onEnable(plugin: JavaPlugin) {
        transaction {
            SchemaUtils.create(UserFatigues, FatigueAreas, Beds)
            Bed.initial()
        }
        UserFatigueConfig.load()
        AreaFatigueConfig.load()
        FatigueCommand().register(plugin)
        BedCommand().register(plugin)

        val itemScheduler = ItemFatigueScheduler(plugin, this, 10000)
        val zoneScheduler = AreaFatigueScheduler(plugin, this, 10000)
        val walkScheduler = WalkFatigueScheduler(plugin, this, 1000)
        val bedScheduler = BedScheduler(plugin, this, 1000)

        itemScheduler.start()
        zoneScheduler.start()
        walkScheduler.start()
        bedScheduler.start()

        plugin.server.pluginManager.registerEvents(FatigueListener(this, zoneScheduler), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {
    }
}
