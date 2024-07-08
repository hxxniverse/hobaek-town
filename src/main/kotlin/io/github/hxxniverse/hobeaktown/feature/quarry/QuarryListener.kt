package io.github.hxxniverse.hobeaktown.feature.quarry

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.jetbrains.exposed.sql.transactions.transaction

class QuarryListener : Listener {
    @EventHandler
    fun onBlockBreakEvent(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val item = player.inventory.itemInMainHand

        if (transaction { Quarry.all().none { it.inQuarry(block) } }) {
            println("not in quarry")
            return
        }

        event.isCancelled = true

        if (!item.isPickForMining()) {
            println("not pickaxe")
            return
        }

        val availableTypes = listOf(Material.STONE, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE)

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