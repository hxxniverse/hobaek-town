package io.github.hxxniverse.hobeaktown.feature.bee.ui

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin
import io.github.hxxniverse.hobeaktown.feature.bee.BeeFeature
import io.github.hxxniverse.hobeaktown.feature.bee.Beehive
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration
import java.time.LocalDateTime

class BeehiveWaitingUi(private val beehive: Beehive) : CustomInventory("벌통", 54) {
    init {
        background(BACKGROUND)

        val finalTime = beehive.startTime.plusSeconds(BeeFeature.BEEHIVE_DURATION_SECOND.toLong())

        display(3 to 4, 4 to 6, ItemStackBuilder(Material.CLOCK)
            .setDisplayName("§6양봉이 아직 진행중입니다!")
            .addLore("")
            .addLore("§7남은시간:§f " + getRemainingText(LocalDateTime.now(), finalTime))
            .build())

        val task = object : BukkitRunnable() {
            override fun run() {
                if(Duration.between(LocalDateTime.now(), finalTime).toSeconds() <= 0) {
                    player.closeInventory()
                    player.sendMessage("§6[양봉]§7 양봉이 모두 완료되어 보상을 받을 수 있습니다!")

                    cancel()
                    return
                }

                display(
                    3 to 4, 4 to 6, ItemStackBuilder(Material.CLOCK)
                        .setDisplayName("§6양봉이 아직 진행중입니다!")
                        .addLore("")
                        .addLore("§7남은시간:§f " + getRemainingText(LocalDateTime.now(), finalTime))
                        .build()
                )
            }
        }.runTaskTimer(HobeakTownPlugin.plugin, 20L, 20L)

        onInventoryClose {
            task.cancel()
        }
    }

    companion object {
        private fun getRemainingText(now: LocalDateTime, final: LocalDateTime): String {
            val remaining = Duration.between(now, final).toSeconds()
            val hours = remaining / 3600
            val minutes = (remaining % 3600) / 60
            val seconds = remaining % 60

            return buildString {
                if (hours > 0) append(hours.toString() + "시간 ")
                if (minutes > 0) append(minutes.toString() + "분 ")
                append(seconds.toString() + "초 ")
            }
        }
    }
}