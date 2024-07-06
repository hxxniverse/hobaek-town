package io.github.hxxniverse.hobeaktown.feature.fatigue.scheduler

import io.github.hxxniverse.hobeaktown.feature.fatigue.FatigueFeature
import io.github.hxxniverse.hobeaktown.feature.fatigue.util.curFatigue
import io.github.hxxniverse.hobeaktown.util.BaseScheduler
import io.github.hxxniverse.hobeaktown.util.extension.component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class WalkFatigueScheduler(
    private val plugin: JavaPlugin,
    private val feature: FatigueFeature,
    interval: Long
) : BaseScheduler(true, interval) {

    override suspend fun onStart() {
    }

    override suspend fun onEach(count: Int) {
        for (player in Bukkit.getOnlinePlayers()) {
            handlePlayerFatigue(player)
        }
    }

    override fun onStop() {
    }

    private fun handlePlayerFatigue(player: Player) {
        val walkingStartTime = feature.walkingTimes[player] ?: return
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - walkingStartTime

        if (elapsedTime >= 5 * 60 * 1000) {
            decreaseFatigue(player)
            feature.walkingTimes[player] = currentTime
        }
    }

    private fun decreaseFatigue(player: Player) {
        val newFatigue = (player.curFatigue - 1).coerceAtLeast(0)
        player.curFatigue = newFatigue
        player.sendMessage(component("현재 피로도: ", NamedTextColor.BLUE).append(component("${player.curFatigue}", NamedTextColor.WHITE)))
    }
}