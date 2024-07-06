package io.github.hxxniverse.hobeaktown.feature.fatigue.scheduler

import io.github.hxxniverse.hobeaktown.feature.fatigue.FatigueFeature
import io.github.hxxniverse.hobeaktown.feature.fatigue.util.curFatigue
import io.github.hxxniverse.hobeaktown.feature.fatigue.util.maxFatigue
import io.github.hxxniverse.hobeaktown.util.BaseScheduler
import io.github.hxxniverse.hobeaktown.util.extension.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction

class AreaFatigueScheduler(
    private val plugin: JavaPlugin,
    private val feature: FatigueFeature,
    interval: Long
) : BaseScheduler(true, interval) {

    override suspend fun onStart() {
    }

    override suspend fun onEach(count: Int) {
        for (player in Bukkit.getOnlinePlayers()) {
            handleZoneFatigue(player)
        }
    }

    override fun onStop() {
    }

    private fun handleZoneFatigue(player: Player) {
        val zone = feature.playerZones[player]
        if (zone != null && shouldChangeFatigue(player, zone.cycle)) {
            updateFatigueFromArea(player, zone.fatigue, zone.isMinus)
        }
    }

    private fun shouldChangeFatigue(player: Player, cycle: Int): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastChangeTime = feature.lastChangeAreaTimes[player] ?: 0L
        val elapsedTime = currentTime - lastChangeTime

        return elapsedTime >= cycle * 60000
    }

    private fun updateFatigueFromArea(player: Player, fatigue: Int, isMinus: Boolean) {
        if(fatigue == 0) return;
        transaction {
            val newFatigue = if(isMinus) {
                (player.curFatigue - fatigue).coerceAtLeast(0)
            } else {
                (player.curFatigue + fatigue).coerceAtMost(player.maxFatigue)
            }

            player.curFatigue = newFatigue

            val fatigueChange = if (isMinus) "감소" else "증가"
            player.sendMessage("해당 지역의 효과로 인해 피로도가 ${fatigueChange}했습니다.")
            player.sendMessage(text("현재 피로도: ", NamedTextColor.BLUE).append(text("${player.curFatigue}", NamedTextColor.WHITE)))
            feature.lastChangeAreaTimes[player] = System.currentTimeMillis()
        }
    }
}