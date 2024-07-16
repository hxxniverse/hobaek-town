package io.github.hxxniverse.hobeaktown.feature.delivery_service

import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBox
import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBoxes
import io.github.hxxniverse.hobeaktown.feature.delivery_service.ui.DeliveryBoxItemSetUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.command_help.help
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class DeliveryServiceCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("택배") {
                then("도움말") {
                    executes {
                        help("택배") {
                            command("택배 보내기 플레이어 <target> <name>") {
                                description = "플레이어에게 택배를 보냅니다."
                            }
                            command("택배 보내기 모두 <name>") {
                                description = "모든 플레이어에게 택배를 보냅니다."
                            }
                            command("택배 생성 <name>") {
                                description = "택배 상자를 생성합니다."
                            }
                            command("택배 가져오기 <name>") {
                                description = "택배 상자를 가져옵니다."
                            }
                            command("택배 수정 <name>") {
                                description = "택배 상자를 수정합니다."
                            }
                            command("택배 삭제 <name>") {
                                description = "택배 상자를 삭제합니다."
                            }
                        }
                    }
                }
                then("보내기") {
                    then("player") {
                        then("target" to player()) {
                            then("name" to string()) {
                                executes {
                                    val target: Player by it
                                    val name: String by it

                                    loggedTransaction {
                                        val deliveryBox = DeliveryBox.find { DeliveryBoxes.name eq name }.firstOrNull()

                                        if (deliveryBox == null) {
                                            sender.sendMessage("존재하지 않는 택배: $name")
                                            return@loggedTransaction
                                        }

                                        // TODO Send
                                    }

                                    target.sendMessage("택배가 도착했습니다: $name")
                                    sender.sendMessage("해당 플레이어에게 택배가 전송되었습니다: $name")
                                }
                            }
                        }
                    }
                    then("all") {
                        then("name" to string()) {
                            executes {
                                val name: String by it

                                loggedTransaction {
                                    val deliveryBox = DeliveryBox.find { DeliveryBoxes.name eq name }.firstOrNull()

                                    if (deliveryBox == null) {
                                        sender.sendMessage("존재하지 않는 택배: $name")
                                        return@loggedTransaction
                                    }

                                    Bukkit.getOnlinePlayers().forEach { target ->
                                        // TODO Send Mail
                                        target.sendMessage("택배가 도착했습니다: $name")
                                    }
                                }

                                sender.sendMessage("모든 플레이어에게 택배가 전송되었습니다: $name")
                            }
                        }
                    }
                }
                then("create") {
                    then("name" to string()) {
                        executes {
                            val name: String by it

                            if (player.inventory.itemInMainHand.type.isAir) {
                                sender.sendMessage("손에 아이템을 들고 실행해주세요.")
                                return@executes
                            }

                            if (loggedTransaction {
                                    DeliveryBox.find { DeliveryBoxes.name eq name }.firstOrNull() != null
                                }) {
                                sender.sendMessage("이미 존재하는 택배: $name")
                                return@executes
                            }

                            loggedTransaction {
                                DeliveryBox.new {
                                    this.name = name
                                    this.boxItem = player.inventory.itemInMainHand
                                }
                            }

                            sender.sendMessage("택배 상자가 생성되었습니다: $name\n수정 명령어를 통해 아이템을 추가할 수 있습니다.")
                        }
                    }
                }
                then("get") {
                    then("name" to string()) {
                        executes {
                            val name: String by it

                            loggedTransaction {
                                val box = DeliveryBox.find { DeliveryBoxes.name eq name }.firstOrNull()

                                if (box == null) {
                                    sender.sendMessage("존재하지 않는 택배: $name")
                                    return@loggedTransaction
                                }

                                println(box)

                                player.inventory.addItem(box.boxItem)
                                player.sendMessage("택배 상자를 받았습니다: $name")
                            }
                        }
                    }
                }
                then("edit") {
                    then("name" to string()) {
                        executes {
                            val name: String by it

                            loggedTransaction {
                                val box = DeliveryBox.find { DeliveryBoxes.name eq name }.firstOrNull()

                                if (box == null) {
                                    sender.sendMessage("존재하지 않는 택배: $name")
                                    return@loggedTransaction
                                }

                                DeliveryBoxItemSetUi(box).open(player)
                            }
                        }
                    }
                }
                then("delete") {
                    then("name" to string()) {
                        executes {
                            val name: String by it

                            loggedTransaction {
                                val box = DeliveryBox.find { DeliveryBoxes.name eq name }.firstOrNull()
                                if (box == null) {
                                    sender.sendMessage("존재하지 않는 택배: $name")
                                    return@loggedTransaction
                                }

                                box.delete()
                            }


                            sender.sendMessage("택배 상자가 삭제되었습니다: $name")
                        }
                    }
                }
            }
        }
    }
}