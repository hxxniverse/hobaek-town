package io.github.hxxniverse.hobeaktown.feature.mainmenu

import io.github.hxxniverse.hobeaktown.feature.mainmenu.ui.DayRewardSetUi
import io.github.hxxniverse.hobeaktown.feature.mainmenu.ui.TotalRewardSetUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.kommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class MainMenuCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("메인메뉴") {
                requires { sender is Player && sender.isOp }
                then("일일보상") {
                    executes {
                        DayRewardSetUi().open(player)
                    }
                }
                then("누적보상") {
                    executes {
                        TotalRewardSetUi().open(player)
                    }
                }
            }
        }
    }
}