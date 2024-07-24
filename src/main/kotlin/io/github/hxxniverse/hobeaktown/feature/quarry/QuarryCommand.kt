package io.github.hxxniverse.hobeaktown.feature.quarry

import io.github.hxxniverse.hobeaktown.feature.area.pos1
import io.github.hxxniverse.hobeaktown.feature.area.pos2
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.command_help.help
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

class QuarryCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("quarry") {
                then("help") {
                    executes {
                        help("quarry") {
                            command("quarry register <name>") {
                                description = "광산을 등록합니다."
                            }
                            command("quarry pickaxe") {
                                description = "곡갱이를 등록합니다."
                            }
                            command("quarry upgrade-item") {
                                description = "강화석 아이템을 설정합니다."
                            }
                        }
                    }
                }
                then("pickaxe") {
                    executes {
                        loggedTransaction {
                            val item = player.inventory.itemInMainHand

                            if (item.type == Material.AIR) {
                                player.sendMessage("아이템을 들고 명령어를 입력해주세요.")
                                return@loggedTransaction
                            }

                            if (!item.isPickForMining()) {
                                player.inventory.setItemInMainHand(item.setPickForMining())
                                player.sendMessage("곡갱이가 등록되었습니다.")
                            } else {
                                player.sendMessage("이미 등록된 곡갱이 입니다.")
                            }
                        }
                    }
                }
                then("upgrade-item") {
                    executes {
                        loggedTransaction {
                            val item = player.inventory.itemInMainHand

                            QuarryConfig.updateConfigData {
                                copy(
                                    upgradeStone = upgradeStone.copy(
                                        itemStack = item
                                    )
                                )
                            }
                            player.sendMessage("강화석 아이템이 설정되었습니다.")
                        }
                    }
                }
            }
        }
    }
}