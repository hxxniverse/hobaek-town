package io.github.hxxniverse.hobeaktown.feature.school

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

object QuestionTask {
    fun startTimer(plugin: JavaPlugin, player: Player) {
        val bossBar = Bukkit.createBossBar("타이머", BarColor.RED, BarStyle.SOLID)
        bossBar.addPlayer(player)

        object : BukkitRunnable() {
            var timeLeft = 60 // 60초 타이머

            override fun run() {
                if (timeLeft > 0) {
                    val progress = timeLeft / 60.0
                    bossBar.progress = progress
                    bossBar.setTitle("남은 시간: $timeLeft 초")
                    timeLeft--
                } else {
                    bossBar.setTitle("타임업!")
                    bossBar.progress = 0.0
                    bossBar.removeAll()
                    this.cancel()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1초 간격으로 실행
    }
}