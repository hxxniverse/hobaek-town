package io.github.hxxniverse.hobeaktown.feature.fatigue.scheduler

import io.github.hxxniverse.hobeaktown.feature.fatigue.FatigueFeature
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.Bed
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.Beds
import io.github.hxxniverse.hobeaktown.feature.fatigue.util.curFatigue
import io.github.hxxniverse.hobeaktown.feature.fatigue.util.maxFatigue
import io.github.hxxniverse.hobeaktown.util.BaseScheduler
import io.github.hxxniverse.hobeaktown.util.extension.component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction

class BedScheduler(
    private val plugin: JavaPlugin,
    private val feature: FatigueFeature,
    interval: Long
): BaseScheduler(true, interval) {
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
        transaction {
            val playerBedStatus = feature.playerBedTime[player] ?: return@transaction
            val bed = Bed.find { Beds.color eq playerBedStatus.first }.firstOrNull() ?: return@transaction
            val cycle = bed.cycle
            val fatigue = bed.fatigue

            val currentTime = System.currentTimeMillis()
            val lastTime = playerBedStatus.second
            val elapsedTime = currentTime - lastTime

            if(elapsedTime >= cycle * 1000){
                increaseFatigue(player, fatigue)
                feature.playerBedTime[player] = Pair(playerBedStatus.first, currentTime)
            }
        }
    }

    private fun increaseFatigue(player: Player, amount: Int) {
        val newFatigue = (player.curFatigue + amount).coerceAtMost(player.maxFatigue)
        player.curFatigue = newFatigue
        player.sendMessage("침대에 머물러 피로도가 증가했습니다.")
        player.sendMessage(component("현재 피로도: ", NamedTextColor.BLUE).append(component("${player.curFatigue}", NamedTextColor.WHITE)))
    }
}