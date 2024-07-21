package io.github.hxxniverse.hobeaktown.feature.police

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.command_help.help
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PoliceCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("경찰") {
                then("도움말") {
                    executes {
                        help("경찰") {
                            command("경찰 수배 <target> <level>") { description = "수배를 걸 수 있습니다." }
                            command("경찰 수배 해제 <target>") { description = "수배를 해제할 수 있습니다." }
                            command("경찰 수배 관리") { description = "모든 플레이어의 수배 상황을 보고 조정할 수 있습니다." }
                            command("경찰 무전 <message>") { description = "모든 플레이어에게 메시지를 보낼 수 있습니다." }
                            command("경찰 정보망 create <minute>") { description = "정보망을 생성할 수 있습니다." }
                            command("경찰 교도소 <target> <minute>") { description = "교도소로 이동시킬 수 있습니다." }
                            command("경찰 교도소 방설정 <number>") { description = "교도소 방을 설정할 수 있습니다." }
                            command("경찰 교도소 시간추가 <target> <minute>") { description = "교도소 수감 시간을 추가할 수 있습니다." }
                            command("경찰 교도소 남은시간") { description = "교도소 수감 시간을 확인할 수 있습니다." }
                            command("경찰 출소") { description = "교도소에서 출소할 수 있습니다." }
                            command("경찰 삼단봉 아이템설정") { description = "삼단봉 아이템을 설정할 수 있습니다." }
                            command("경찰 삼단봉 생성 <damage>") { description = "삼단봉을 생성할 수 있습니다." }
                            command("경찰 수갑 아이템설정") { description = "수갑 아이템을 설정할 수 있습니다." }
                            command("경찰 수갑 생성 <damage>") { description = "수갑을 생성할 수 있습니다." }
                            command("경찰 구치소 위치설정") { description = "구치소 위치를 설정할 수 있습니다." }
                        }
                    }
                }
                then("수배") {
                    then("target" to player()) {
                        then("level" to int()) {
                            executes {
                                val target: Player by it
                                val level: Int by it

                                WantedManager.addWanted(target.uniqueId, level)
                                player.sendMessage("수배가 등록되었습니다.")
                            }
                        }
                    }
                    then("해제") {
                        then("target" to player()) {
                            executes {
                                val target: Player by it

                                WantedManager.removeWanted(target.uniqueId)
                                player.sendMessage("수배가 해제되었습니다.")
                            }
                        }
                    }
                    then("관리") {
                        executes {
                            // TODO 모든 플레이어의 수배 상황을 보고 조정할 수 있습니다.
                        }
                    }
                }

                then("무전") {
                    then("message" to string()) {
                        executes {
                            val message: String by it
                        }
                    }
                }

                then("정보망") {
                    then("create") {
                        then("minute" to int()) {
                            executes {
                                val minute: Int by it
                            }
                        }
                    }
                }

                then("교도소") {
                    then("target" to player()) {
                        then("minute" to int()) {
                            executes {
                                val target: Player by it
                                val minute: Int by it

                                // TODO 교도소로 이동
                                IncarcerationManager.addIncarceration(target, minute)
                                player.sendMessage("교도소에 수감되었습니다.")
                            }
                        }
                    }
                    then("방설정") {
                        then("number" to int()) {
                            executes {
                                val number: Int by it
                            }
                        }
                    }
                    then("시간추가") {
                        then("target" to player()) {
                            then("minute" to int()) {
                                executes {
                                    val target: Player by it
                                    val minute: Int by it

                                    IncarcerationManager.extendIncarceration(target, minute)
                                    player.sendMessage("교도소 수감 시간이 추가되었습니다.")
                                }
                            }
                        }
                    }
                    then("남은시간") {
                        executes {
                            IncarcerationManager.releaseIncarceration(player)
                        }
                    }
                }


                then("출소") {
                    executes {
                        IncarcerationManager.releaseIncarceration(player)
                    }
                }

                then("삼단봉") {
                    then("아이템설정") {
                        executes {
                            if (player.inventory.itemInMainHand.type.isAir) {
                                player.sendMessage("아이템을 들고 명령어를 입력해주세요.")
                                return@executes
                            }

                            PoliceConfig.updateConfigData {
                                copy(threeStepStickItem = player.inventory.itemInMainHand)
                            }
                        }
                    }
                    then("생성") {
                        then("damage" to int()) {
                            executes {
                                val damage: Int by it
                            }
                        }
                    }
                }

                then("수갑") {
                    then("아이템설정") {
                        executes {
                            if (player.inventory.itemInMainHand.type.isAir) {
                                player.sendMessage("아이템을 들고 명령어를 입력해주세요.")
                                return@executes
                            }

                            PoliceConfig.updateConfigData {
                                copy(handcuffsItem = player.inventory.itemInMainHand)
                            }
                        }
                    }
                    then("생성") {
                        then("damage" to int()) {
                            executes {
                                val damage: Int by it
                            }
                        }
                    }
                }

                then("구치소") {
                    then("위치설정") {
                        executes {
                            PoliceConfig.updateConfigData {
                                copy(detentionCenterLocation = player.location)
                            }
                        }
                    }
                }
            }
        }
    }
}