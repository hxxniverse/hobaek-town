package io.github.hxxniverse.hobeaktown.feature.quarry

import io.github.hxxniverse.hobeaktown.feature.area.Area
import io.github.hxxniverse.hobeaktown.feature.area.AreaType
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class QuarryListener : Listener {
    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val item = player.inventory.itemInMainHand

        val area = loggedTransaction { Area.all().find { it.inArea(block) } }

        if (area == null) {
            return
        }

        if (area.type != AreaType.MINE) {
            return
        }

        event.isCancelled = true

        if (!item.isPickForMining()) {
            return
        }

        block.getDrops(item).forEach { player.inventory.addItem(it) }

        val availableTypes = listOf(
            Material.STONE,
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE
        )

        if (availableTypes.contains(block.type)) {
            val random = Math.random()
            val config = QuarryConfig.configData

            if (random < config.mineralProbability.stone) {
                block.type = Material.STONE
            } else if (random < config.mineralProbability.stone + config.mineralProbability.coal) {
                block.type = Material.COAL_ORE
            } else if (random < config.mineralProbability.stone + config.mineralProbability.coal + config.mineralProbability.iron) {
                block.type = Material.IRON_ORE
            } else if (random < config.mineralProbability.stone + config.mineralProbability.coal + config.mineralProbability.iron + config.mineralProbability.gold) {
                block.type = Material.GOLD_ORE
            } else if (random < config.mineralProbability.stone + config.mineralProbability.coal + config.mineralProbability.iron + config.mineralProbability.gold + config.mineralProbability.diamond) {
                block.type = Material.DIAMOND_ORE
            } else if (random < config.mineralProbability.stone + config.mineralProbability.coal + config.mineralProbability.iron + config.mineralProbability.gold + config.mineralProbability.diamond + config.mineralProbability.emerald) {
                block.type = Material.EMERALD_ORE
            }

            if (Math.random() < config.upgradeStone.chance) {
                player.inventory.addItem(config.upgradeStone.itemStack)
            }
        }
    }
}