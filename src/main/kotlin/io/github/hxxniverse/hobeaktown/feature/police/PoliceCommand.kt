package io.github.hxxniverse.hobeaktown.feature.police

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PoliceCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("수배") {
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

            register("무전") {
                then("message" to string()) {
                    executes {
                        val message: String by it
                    }
                }
            }

            register("정보망") {
                then("create") {
                    then("minute" to int()) {
                        executes {
                            val minute: Int by it
                        }
                    }
                }
            }

            register("교도소") {
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


            register("출소") {
                executes {
                    IncarcerationManager.releaseIncarceration(player)
                }
            }

            register("삼단봉") {
                then("아이템성절") {
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

            register("수갑") {
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

            register("구치소") {
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