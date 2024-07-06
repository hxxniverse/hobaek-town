package io.github.hxxniverse.hobeaktown.feature.fatigue.scheduler;

import com.mojang.authlib.yggdrasil.response.User
import io.github.hxxniverse.hobeaktown.feature.fatigue.FatigueFeature
import io.github.hxxniverse.hobeaktown.feature.fatigue.config.UserItemFatigueData
import io.github.hxxniverse.hobeaktown.feature.fatigue.util.curFatigue
import io.github.hxxniverse.hobeaktown.util.BaseScheduler
import io.github.hxxniverse.hobeaktown.util.extension.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction

class ItemFatigueScheduler(
    private val plugin: JavaPlugin,
    private val feature: FatigueFeature,
    interval: Long
) : BaseScheduler(true, interval) {

    override suspend fun onStart() {}

    override suspend fun onEach(count: Int) {
        for (player in Bukkit.getOnlinePlayers()) {
            handlePlayerFatigue(player)
        }
    }

    override fun onStop() {}


    private fun handlePlayerFatigue(player: Player) {
        val currentTime = System.currentTimeMillis()
        player.inventory.forEach { item ->
            if (item != null && isUnusable(item)) {
                val fatigueData: UserItemFatigueData? = getFatigueData(item)
                if (fatigueData != null) {
                    val playerFatigueList = feature.playerFatigueItem.getOrPut(player) { mutableListOf() }
                    val existingData = playerFatigueList.find { it.id == fatigueData.id }

                    if (existingData == null) {
                        fatigueData.lastChangeTime = currentTime
                        playerFatigueList.add(fatigueData)
                    } else {
                        fatigueData.lastChangeTime = existingData.lastChangeTime
                    }

                    if (shouldDecreaseFatigue(fatigueData, currentTime)) {
                        decreaseFatigue(player, fatigueData.fatigue)
                        updatePlayerFatigueItem(player, fatigueData, currentTime)
                    }
                }
            }
        }
    }

    private fun getFatigueData(item: ItemStack): UserItemFatigueData? {
        val meta = item.itemMeta ?: return null
        val idKey = NamespacedKey(plugin, "id")
        val cycleKey = NamespacedKey(plugin, "cycle")
        val fatigueKey = NamespacedKey(plugin, "fatigue")

        val id = meta.persistentDataContainer.get(idKey, PersistentDataType.LONG)
        val cycle = meta.persistentDataContainer.get(cycleKey, PersistentDataType.INTEGER)
        val fatigue = meta.persistentDataContainer.get(fatigueKey, PersistentDataType.INTEGER)

        return if(id != null && cycle != null && fatigue != null) UserItemFatigueData(id, cycle, fatigue, 0L) else null
    }

    private fun shouldDecreaseFatigue(data: UserItemFatigueData, currentTime: Long): Boolean {
        return (currentTime - data.lastChangeTime) >= data.cycle * 60 * 1000
    }

    private fun decreaseFatigue(player: Player, fatigue: Int) {
        transaction {
            val newFatigue = (player.curFatigue - fatigue).coerceAtLeast(0)
            player.curFatigue = newFatigue
            player.sendMessage("인벤토리의 아이템 효과로 인해 피로도가 감소했습니다.")
            player.sendMessage(text("현재 피로도: ", NamedTextColor.BLUE).append(text("${player.curFatigue}", NamedTextColor.WHITE)))
        }
    }

    private fun updatePlayerFatigueItem(player: Player, data: UserItemFatigueData, currentTime: Long) {
        val fatigueList = feature.playerFatigueItem[player] ?: mutableListOf()
        val existingData = fatigueList.find { it.id == data.id }
        if (existingData != null) {
            existingData.lastChangeTime = currentTime
        } else {
            data.lastChangeTime = currentTime
            fatigueList.add(data)
        }
        feature.playerFatigueItem[player] = fatigueList
    }

    private fun isUnusable(item: ItemStack): Boolean {
        val meta = item.itemMeta ?: return false
        val key = NamespacedKey(plugin, "unusable")
        return meta.persistentDataContainer.has(key, PersistentDataType.BYTE)
    }
}