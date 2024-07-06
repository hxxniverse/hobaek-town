package io.github.hxxniverse.hobeaktown.feature.police

import org.bukkit.entity.Player
import java.time.LocalDateTime
import java.util.*

object IncarcerationManager {
    private val incarcerations = mutableMapOf<UUID, Incarceration>()

    fun addIncarceration(player: Player, minute: Int) {
        incarcerations[player.uniqueId] =
            Incarceration(player.uniqueId, LocalDateTime.now().plusMinutes(minute.toLong()))
    }

    fun removeIncarceration(player: Player) {
        incarcerations.remove(player.uniqueId)
    }

    fun extendIncarceration(player: Player, minute: Int) {
        val incarceration = incarcerations[player.uniqueId]
        if (incarceration != null) {
            incarcerations[player.uniqueId] =
                incarceration.copy(releaseTime = incarceration.releaseTime.plusMinutes(minute.toLong()))
        }
    }

    fun releaseIncarceration(player: Player) {
        val incarceration = incarcerations[player.uniqueId]

        if (incarceration != null) {
            if (LocalDateTime.now().isAfter(incarceration.releaseTime)) {
                incarcerations.remove(player.uniqueId)
            } else {
                player.sendMessage("아직은 출소 시간이 아닙니다.")
                player.sendMessage(
                    "남은시간: ${
                        LocalDateTime.now().until(incarceration.releaseTime, java.time.temporal.ChronoUnit.MINUTES)
                    }분"
                )
            }
        } else {
            player.sendMessage("교도소 수감된 플레이어만 사용가능합니다.")
        }
    }
}

data class Incarceration(
    val uuid: UUID,
    val releaseTime: LocalDateTime
)
