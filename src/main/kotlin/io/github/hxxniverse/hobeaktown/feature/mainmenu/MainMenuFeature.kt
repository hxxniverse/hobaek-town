package io.github.hxxniverse.hobeaktown.feature.mainmenu

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin
import io.github.hxxniverse.hobeaktown.feature.mainmenu.entity.*
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import java.time.LocalTime

class MainMenuFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(DayRewards, TotalRewards, Playtimes, DayRewardClaims, TotalRewardClaims)
        }
        MainMenuCommand().register(plugin)
        Bukkit.getPluginManager().registerEvents(MainMenuListener(), HobeakTownPlugin.plugin)

        startScheduler()
    }

    override fun onDisable(plugin: JavaPlugin) {
    }

    private fun startScheduler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(HobeakTownPlugin.plugin, Runnable {
            val now = LocalTime.now()

            loggedTransaction {
                if(now.hour == 2 && now.minute == 32) {
                    DayRewardClaim.resetAllClaims()
                    TotalRewardClaim.resetAllClaims()

                    Playtime.resetAllDayPlaytime()
                }

                for(player in Bukkit.getOnlinePlayers()) {
                    Playtime.addPlaytime(player.uniqueId, 1)
                }
            }
        }, 1200L, 1200L) //
    }
}