package io.github.hxxniverse.hobeaktown.feature.traffic

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.plugin.java.JavaPlugin

class TrafficCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("traffic") {
                then("bus") {
                    then("station") {
                        executes {
                            sender.sendMessage("버스역 지정")
                        }
                    }
                    then("scenery") {
                        executes {
                            sender.sendMessage("버스역 풍경")
                        }
                    }
                    then("order") {
                        executes {
                            sender.sendMessage("버스역 순서")
                        }
                    }
                    then("ticket") {
                        then("create") {
                            then("amount" to int()) {
                                executes {
                                    val amount: Int by it

                                    if (amount <= 0) {
                                        sender.sendMessage("0보다 큰 수를 입력해주세요.")
                                        return@executes
                                    }

                                    player.inventory.addItem(TrafficConfig.configData.busTicket.clone().apply {
                                        setAmount(amount)
                                    })
                                    player.sendMessage("버스 티켓을 ${amount}개 지급하였습니다.")
                                }
                            }
                        }
                        then("item") {
                            executes {
                                if (player.inventory.itemInMainHand.type.isAir) {
                                    sender.sendMessage("아이템을 들고 명령어를 입력해주세요.")
                                    return@executes
                                }

                                TrafficConfig.updateConfigData {
                                    copy(busTicket = player.inventory.itemInMainHand)
                                }
                                player.sendMessage("버스 티켓 아이템이 설정되었습니다.")
                            }
                        }
                    }
                }
            }
        }
    }
}