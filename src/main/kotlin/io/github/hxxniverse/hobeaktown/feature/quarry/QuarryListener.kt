package io.github.hxxniverse.hobeaktown.feature.quarry

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

        if (!item.isPickForMining()) {
            return
        }

        if (Quarry.all().none { it.inQuarry(block) }) {
            return
        }

        if (block.type == Material.STONE) {
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