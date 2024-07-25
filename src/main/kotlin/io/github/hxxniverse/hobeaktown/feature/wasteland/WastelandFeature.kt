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
                if(wasteland.material == Material.GRAVEL) wasteland.material = Material.SUSPICIOUS_GRAVEL
                if(wasteland.material == Material.SAND) wasteland.material = Material.SUSPICIOUS_SAND

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
                // 현재 레벨에 따라 제외할 인덱스 제외
                if (shouldExclude(index, level)) {
                    continue
                }

                val weight = getWeight(index)
                if (weight > 0) {
                    map[itemStack] = weight
                }
            }

            // map 에 등록된 아이템이 없을 경우 리턴 (이론상 불가능 / 버그 방지)
            if (map.isEmpty()) return ItemStack(Material.AIR)

            // 확장 함수를 사용하여 랜덤한 ItemStack 뽑기
            return map.weightedRandomFromList()
        }

        private fun shouldExclude(index: Int, level: Int): Boolean {
            val base = listOf(9, 36)
            val excludes = mutableSetOf<Int>()

            for (i in 0 until level - 1) {
                excludes.add(base[0] + i)
                excludes.add(base[1] + i)
            }

            return excludes.contains(index)
        }

        private fun getWeight(index: Int): Int {
            return when (index) {
                9, 36 -> 60
                10, 37 -> 50
                11, 38 -> 30
                12, 39 -> 20
                13, 40 -> 16
                14, 41 -> 12
                15, 42 -> 8
                16, 43 -> 3
                17, 44 -> 1
                else -> 0
            }
        }
    }
}