package io.github.hxxniverse.hobeaktown.feature.school;

import io.github.hxxniverse.hobeaktown.feature.user.Users
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.jetbrains.exposed.sql.SchemaUtils

class SchoolFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(UserGrades)
        }
        SchoolCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {
    }
}
