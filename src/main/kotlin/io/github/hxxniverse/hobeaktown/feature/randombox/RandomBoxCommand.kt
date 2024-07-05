package io.github.hxxniverse.hobeaktown.feature.randombox

import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBox
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxes
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * 명령어 리스트 ( Command list )
 *
 * /랜덤박스 제작 (박스이름)
 * ㄴ 명령어 입력시 gui 창이 열리고 상자 아이템과 나올 아이템을 두고 닫으면 완성됩니다.
 * /랜덤박스 삭제 (박스이름)
 * /랜덤박스 수정 (박스이름)
 * /랜덤박스 리스트
 */
class RandomBoxCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("randombox") {
                then("create") {
                    then("name" to string()) {
                        executes {
                            transaction {
                                val name: String by it

                                if (RandomBox.find { RandomBoxes.name eq name }.firstOrNull() != null) {
                                    player.sendMessage("이미 존재하는 이름의 랜덤박스입니다.")
                                    return@transaction
                                }

                                val randomBox = RandomBox.new {
                                    this.name = name
                                    this.itemStack = ItemStackBuilder(Material.CHEST)
                                        .setDisplayName("§6§l$name 랜덤박스")
                                        .addLore("")
                                        .addLore("§7이 랜덤박스를 우클릭하여 열어보세요.")
                                        .addLore("§7이 랜덤박스는 랜덤으로 아이템을 획득할 수 있습니다.")
                                        .addLore("§7좌클릭 하여 확률을 확인할 수 있습니다.")
                                        .build()
                                }

                                RandomBoxSetItemUi(randomBox).open(player)
                                player.sendMessage("$randomBox 랜덤박스가 생성되었습니다.")
                            }
                        }
                    }
                }
                then("delete") {
                    then("name" to string()) {
                        executes {
                            transaction {
                                val name: String by it

                                val randomBox = RandomBox.find { RandomBoxes.name eq name }.firstOrNull()

                                if (randomBox == null) {
                                    player.sendMessage("해당 이름의 랜덤박스가 존재하지 않습니다.")
                                    return@transaction
                                }

                                randomBox.delete()
                                player.sendMessage("$randomBox 랜덤박스가 삭제되었습니다.")
                            }
                        }
                    }
                }
                then("edit") {
                    then("name" to string()) {
                        executes {
                            transaction {
                                val name: String by it

                                val randomBox = RandomBox.find { RandomBoxes.name eq name }.firstOrNull()

                                if (randomBox == null) {
                                    player.sendMessage("해당 이름의 랜덤박스가 존재하지 않습니다.")
                                    return@transaction
                                }

                                RandomBoxSetItemUi(randomBox).open(player)
                                player.sendMessage("$randomBox 랜덤박스를 수정합니다.")
                            }
                        }
                    }
                }
                then("list") {
                    executes {
                        transaction {
                            val randomBoxes = RandomBox.all()
                            randomBoxes.forEach {
                                sender.sendMessage(it.name)
                            }
                        }
                    }
                }
                then("get") {
                    then("name" to string()) {
                        executes {
                            transaction {
                                val name: String by it

                                val randomBox = RandomBox.find { RandomBoxes.name eq name }.firstOrNull()

                                if (randomBox == null) {
                                    player.sendMessage("해당 이름의 랜덤박스가 존재하지 않습니다.")
                                    return@transaction
                                }

                                player.inventory.addItem(randomBox.itemStack)
                            }
                        }
                    }
                }
            }
        }
    }
}