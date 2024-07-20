package io.github.hxxniverse.hobeaktown.feature.bee

import io.github.hxxniverse.hobeaktown.feature.bee.ui.BeehiveEditUi
import io.github.hxxniverse.hobeaktown.feature.bee.ui.BeehiveSetUi
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class BeeCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("양봉") {
                requires { sender is Player && sender.isOp }
                then("상자") {
                    then("목록") {
                        executes {
                            val set = Beehive.getAllCodes()
                            var index = 1

                            set.forEach {
                                sender.sendMessage("[" + (index++) + "] " + it)
                            }
                            sender.sendMessage("§6[양봉]§7 등록된 모든 벌통을 조회하였습니다.")
                        }
                    }
                    then("받기") {
                        then("name" to string()) {
                            executes {
                                val name : String by it

                                if(!Beehive.existCode(name)) {
                                    sender.sendMessage("§6[양봉]§7 해당 이름을 가진 벌통은 존재하지 않습니다.")
                                    return@executes
                                }

                                (sender as Player).inventory.addItem(ItemStackBuilder(Material.BEEHIVE).setDisplayName("벌통 생성 블럭: $name").build())
                                sender.sendMessage("§6[양봉]§7 해당 이름을 가진 벌통을 지급하였습니다.")
                            }
                        }
                    }
                    then("생성") {
                        then("name" to string()) {
                            executes {
                                val name : String by it

                                if(Beehive.existCode(name)) {
                                    sender.sendMessage("§6[양봉]§7 해당 이름의 벌통은 이미 존재합니다.")
                                    return@executes
                                }

                                BeehiveSetUi(name).open(sender as Player)
                            }
                        }
                    }
                    then("수정") {
                        then("name" to string()) {
                            executes {
                                val name : String by it

                                if(!Beehive.existCode(name)) {
                                    sender.sendMessage("§6[양봉]§7 해당 이름의 벌통이 존재하지 않습니다.")
                                    return@executes
                                }

                                BeehiveEditUi(name).open(sender as Player)
                            }
                        }
                    }
                }
            }
        }
    }
}