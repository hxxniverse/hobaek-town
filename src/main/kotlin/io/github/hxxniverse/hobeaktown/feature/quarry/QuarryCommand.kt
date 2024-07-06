package io.github.hxxniverse.hobeaktown.feature.quarry

import io.github.hxxniverse.hobeaktown.sub_feature.pos1
import io.github.hxxniverse.hobeaktown.sub_feature.pos2
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction

class QuarryCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("quarry") {
                then("register") {
                    then("name" to string()) {
                        executes {
                            transaction {
                                val name: String by it

                                val pos1 = player.pos1()
                                val pos2 = player.pos2()

                                if (pos1 == null || pos2 == null) {
                                    player.sendMessage("광산 위치를 설정해주세요.")
                                    return@transaction
                                }

                                Quarry.new {
                                    this.name = name
                                    this.pos1 = pos1
                                    this.pos2 = pos2
                                }
                                player.sendMessage("광산이 등록되었습니다.")
                            }
                        }
                    }
                }
                then("pickaxe") {
                    executes {
                        transaction {
                            val item = player.inventory.itemInMainHand

                            if (item.type == Material.AIR) {
                                player.sendMessage("아이템을 들고 명령어를 입력해주세요.")
                                return@transaction
                            }

                            if (!item.isPickForMining()) {
                                item.setPickForMining()
                                player.sendMessage("곡갱이가 등록되었습니다.")
                            } else {
                                player.sendMessage("이미 등록된 곡갱이 입니다.")
                            }
                        }
                    }
                }
                then("upgrade") {
                    then("item") {
                        executes {
                            transaction {
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
}