package io.github.hxxniverse.hobeaktown.feature.school

import io.github.hxxniverse.hobeaktown.feature.user.Job
import io.github.hxxniverse.hobeaktown.feature.user.User
import io.github.hxxniverse.hobeaktown.feature.user.Users
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or

object QuestionTask {
    fun startTimer(plugin: JavaPlugin) {
        val bossBarKey = NamespacedKey(plugin, "question")
        val existingBossBar: BossBar? = Bukkit.getBossBar(bossBarKey)
        if (existingBossBar != null) {
            existingBossBar.removeAll()
            Bukkit.removeBossBar(bossBarKey)
        }

        val bossBar = Bukkit.createBossBar(bossBarKey, "타이머", BarColor.RED, BarStyle.SOLID)

        loggedTransaction {
            val students = User.find { (Users.job eq Job.STUDENT) or (Users.job eq Job.TEACHER) }.toList()
            students.forEach {it ->
                val player = Bukkit.getPlayer(it.id.value)
                if(player != null) bossBar.addPlayer(player)
            }
        }

        object : BukkitRunnable() {
            var timeLeft = 180

            override fun run() {
                if (timeLeft > 0) {
                    val progress = timeLeft / 180.0
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
        }.runTaskTimer(plugin, 0L, 20L)
    }
}