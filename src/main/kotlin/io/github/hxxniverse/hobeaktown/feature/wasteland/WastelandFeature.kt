package io.github.hxxniverse.hobeaktown.feature.wasteland

import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.*
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils

class WastelandFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(Wastelands, WastelandRewards, WastelandSetups, WastelandSetupRewards, Brushes)
        }
        WastelandCommand().register(plugin)

        Bukkit.getPluginManager().registerEvents(WastelandListener(), plugin)

        loggedTransaction {
            Wasteland.all().forEach { wasteland ->
                if(wasteland.location.block.type != wasteland.material) {
                    wasteland.location.block.type = wasteland.material
                }
            }
        }
    }

    override fun onDisable(plugin: JavaPlugin) {
    }

    companion object {
        private val WAITING_BLOCK : MutableSet<Location> = mutableSetOf()

        fun addWaiting(loc: Location) {
            WAITING_BLOCK.add(loc)
        }

        fun removeWaiting(loc: Location) {
            WAITING_BLOCK.remove(loc)
        }

        fun randomItem(rewards: Map<Int, ItemStack>, level: Int): ItemStack {
            val map: MutableMap<ItemStack, Double> = mutableMapOf()

            // guiItemMap 에서 ItemStack 을 뽑아 map 으로 put 하며 가중치 부여
            for ((index, itemStack) in rewards) {
                // 솔 등급에 따라 확률적으로 받을 수 있는 아이템 거르기
                if (index % 9 >= level) {
                    continue
                }

                map[itemStack] = getWeight(index)
            }

            // map 에 등록된 아이템이 없을 경우 리턴 (이론상 불가능 / 버그 방지)
            if (map.isEmpty()) return ItemStack(Material.AIR)

            val totalWeight = map.values.sum()
            val randomValue = Math.random() * totalWeight
            var cumulativeWeight = 0.0

            // 가중치에 기반하여 랜덤한 ItemStack 뽑기
            for ((itemStack, weight) in map) {
                cumulativeWeight += weight
                if (randomValue <= cumulativeWeight) {
                    return itemStack
                }
            }

            // 랜덤으로 ItemStack 을 획득하지 못했을 경우 (이론상 불가능)
            return ItemStack(Material.AIR)
        }

        private fun getWeight(index: Int): Double {
            return when (index) {
                9, 36 -> 0.30
                10, 37 -> 0.25
                11, 38 -> 0.15
                12, 39 -> 0.10
                13, 40 -> 0.08
                14, 41 -> 0.06
                15, 42 -> 0.04
                16, 43 -> 0.015
                17, 44 -> 0.005
                else -> 0.0
            }
        }
    }
}