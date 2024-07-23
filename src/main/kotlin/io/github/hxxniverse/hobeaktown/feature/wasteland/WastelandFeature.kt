package io.github.hxxniverse.hobeaktown.feature.wasteland

import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.*
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.extension.weightedRandomFromList
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
            val map: MutableMap<ItemStack, Int> = mutableMapOf()

            // guiItemMap 에서 ItemStack 을 뽑아 map 으로 put 하며 가중치 부여
            for ((index, itemStack) in rewards) {
                // 솔 등급에 따라 확률적으로 받을 수 있는 아이템 거르기
                if (index % 9 >= level) {
                    continue
                }

                val weight = getWeight(index) // 가중치를 정수형으로 변환
                if (weight > 0) {
                    map[itemStack] = weight
                }
            }

            // map 에 등록된 아이템이 없을 경우 리턴 (이론상 불가능 / 버그 방지)
            if (map.isEmpty()) return ItemStack(Material.AIR)

            // 확장 함수를 사용하여 랜덤한 ItemStack 뽑기
            return map.weightedRandomFromList()
        }

        private fun getWeight(index: Int): Int {
            return when (index) {
                9, 36 -> 300
                10, 37 -> 250
                11, 38 -> 150
                12, 39 -> 100
                13, 40 -> 80
                14, 41 -> 60
                15, 42 -> 40
                16, 43 -> 15
                17, 44 -> 5
                else -> 0
            }
        }
    }
}