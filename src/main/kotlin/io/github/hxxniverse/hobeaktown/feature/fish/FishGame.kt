package io.github.hxxniverse.hobeaktown.feature.fish

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class FishGame(val player: Player, val reward: ItemStack) {
    companion object {
        private val gamingMap: MutableMap<UUID, FishGame> = mutableMapOf()
        private val letters = listOf("좌", "우", "왼", "오", "L", "R")

        fun startGame(player: Player, reward: ItemStack) {
            val game = FishGame(player, reward)

            if (gamingMap.containsValue(game)) {
                return
            }

            gamingMap[player.uniqueId] = game
            game.startTask()
        }

        fun stopGame(player: Player) {
            gamingMap.remove(player.uniqueId)
        }

        fun isGaming(player: Player): Boolean {
            return gamingMap.containsKey(player.uniqueId)
        }

        fun getGame(player: Player): FishGame? {
            return gamingMap[player.uniqueId]
        }
    }

    val length: Int = (3..9).random()
    val randomText: List<String> = List(length) { letters.random() }
    private var current = 0

    /**
     * @param click 플레이어가 낚시 미니게임 중 입력한 클릭 타입 (0: 좌클릭, 1: 우클릭)
     * @return 반환되는 Int 의 종류는 아래 설명과 같습니다.
     *         0 - Input 실패 (게임 실패)
     *         1 - Input 성공 (게임 진행 중)
     *         2 - 게임 승리
     */
    fun input(click: Int): Int {
        val letter = if(click == 0) {
            listOf("왼", "좌", "L")
        } else {
            listOf("오", "우", "R")
        }

        if(current < randomText.size && randomText[current] in letter) {
            current++

            if (current == randomText.size) {
                return 2
            }
            return 1
        } else {
            return 0
        }
    }

    fun getTitleText(): String {
        return randomText.mapIndexed { index, letter ->
            if (index < current) "§a$letter" else "§7$letter"
        }.joinToString(" ")
    }

    @Suppress("DEPRECATION")
    private fun startTask() {
        val totalTime = (60..100).random() // 3초 ~ 5초
        var timeLeft = totalTime

        object : BukkitRunnable() {
            override fun run() {
                if (!isGaming(player)) {
                    cancel()
                    return
                }

                timeLeft--

                if (timeLeft <= 0) {
                    player.sendTitle("§c실패", "", 4, 20, 4)
                    stopGame(player)
                    cancel()
                } else {
                    player.sendTitle(getTitleText(), "§7남은 시간:§f ${String.format("%.2f", timeLeft / 20.0)}초", 0, 5, 0)
                }
            }
        }.runTaskTimer(HobeakTownPlugin.plugin, 0L, 1L)
    }
}