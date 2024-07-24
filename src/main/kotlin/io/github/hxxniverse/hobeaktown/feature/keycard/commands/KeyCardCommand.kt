package io.github.hxxniverse.hobeaktown.feature.keycard.commands

import io.github.hxxniverse.hobeaktown.feature.keycard.entity.KeyCard
import io.github.hxxniverse.hobeaktown.feature.user.Job
import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.monun.kommand.StringType.GREEDY_PHRASE
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException
import java.util.EnumSet

class KeyCardCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("키카드") {
                requires { sender is Player }
                executes { player.sendMessage("명령어 도우미: /키카드 help") }
                then("help") {
                    executes {
                        """
                        |키카드 명령어
                        |  /키카드 생성 [태그] [이름]: [이름]에 [태그]를 등록하는 키카드를 생성합니다. (아이템 생성 X)
                        |  /키카드 철문 [이름]: 철문을 들고 해당 철문에 권한을 등록합니다. 설치 시 해당되는 키카드로 열고 닫을 수 있습니다.
                        |  /키카드 등록 [이름]: 손에 들고 있는 아이템이 해당 권한의 키카드가 됩니다.
                        """.trimIndent().also {
                            component(it).send(player)
                        }
                    }
                }
                then("생성") {
                    executes { player.sendMessage("명령어 사용법: /키카드 help"); }
                    then("job" to dynamicByEnum(EnumSet.allOf(Job::class.java))) {
                        then("name" to string(GREEDY_PHRASE)) {
                            executes {
                                val job: Job by it
                                val name: String by it

                                loggedTransaction {
                                    try {
                                        if (KeyCard.isExistsKeyCard(name, job)) {
                                            player.sendMessage("이미 등록되어있는 키카드입니다.")
                                        } else {
                                            KeyCard.new {
                                                this.name = name
                                                this.job = job
                                            }
                                            player.sendMessage("키카드 이름: $name 역할: ${job.name} 생성됨.")
                                        }
                                    } catch (e: SQLException) {
                                        player.sendMessage(e.message!!)
                                    }
                                }
                            }
                        }
                    }
                }
                then("철문") {
                    executes { player.sendMessage("명령어 사용법: /키카드 help"); }
                    then("name" to string(GREEDY_PHRASE)) {
                        executes {
                            val name: String by it

                            loggedTransaction {
                                val itemInHand = player.inventory.itemInMainHand
                                if (itemInHand.type != Material.IRON_DOOR) {
                                    player.sendMessage("철문을 손에 들고 명령어를 입력해 주세요.")
                                    return@loggedTransaction
                                }

                                if (KeyCard.isExistsKeyName(name)) {
                                    val keyCardDoorItem = ItemStackBuilder()
                                        .setType(itemInHand.type)
                                        .setDisplayName(
                                            component(name, NamedTextColor.BLUE)
                                                .append(component(" 철문", NamedTextColor.WHITE))
                                        )
                                        .addPersistentData("NameRegister", "true")
                                        .addPersistentData("Name", name)
                                        .build()

                                    player.inventory.setItemInMainHand(keyCardDoorItem)
                                    player.sendMessage("철문에 $name 이름 등록됨.")
                                } else player.sendMessage("'$name'은 등록되지 않은 이름입니다.")
                            }
                        }
                    }
                }
                then("등록") {
                    executes { player.sendMessage("명령어 사용법: /키카드 help"); }
                    then("name" to string(GREEDY_PHRASE)) {
                        executes {
                            val name: String by it

                            loggedTransaction {
                                var itemInHand = player.inventory.itemInMainHand
                                if (itemInHand.type == Material.IRON_DOOR) return@loggedTransaction
                                if (itemInHand.isEmpty) {
                                    player.sendMessage("아이템을 손에 들고 명령어를 입력해 주세요.")
                                    return@loggedTransaction
                                }

                                if (KeyCard.isExistsKeyName(name)) {
                                    itemInHand = ItemStackBuilder()
                                        .setType(itemInHand.type)
                                        .setDisplayName(
                                            component(name, NamedTextColor.BLUE)
                                                .append(component(" 키카드", NamedTextColor.WHITE))
                                        )
                                        .addPersistentData("NameRegister", "true")
                                        .addPersistentData("Name", name)
                                        .build()

                                    player.inventory.setItemInMainHand(itemInHand)
                                    player.sendMessage("아이템이 $name 키카드로 등록되었습니다.")
                                } else player.sendMessage("'$name'은 등록되지 않은 이름입니다.")
                            }
                        }
                    }
                }
                then("변경"){
                    requires { sender.isOp }
                    then("player" to player()){
                        then("job" to string()){
                            executes {
                                val player: Player by it
                                val job: String by it
                                loggedTransaction {
                                    player.user.job = Job.valueOf(job)
                                    player.sendMessage(component(job, NamedTextColor.BLUE).append(component("직업으로 변경되었습니다.", NamedTextColor.WHITE)))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}