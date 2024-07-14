package io.github.hxxniverse.hobeaktown.feature.keycard.commands

import io.github.monun.kommand.StringType.GREEDY_PHRASE;
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.KeyCard
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Role
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Roles
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

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
                        |  /키카드 생성 [이름] [태그]: [이름]에 [태그]를 등록하는 키카드를 생성합니다. (아이템 생성 X)
                        |  /키카드 철문 [이름]: 철문을 들고 해당 철문에 권한을 등록합니다. 설치 시 해당되는 키카드로 열고 닫을 수 있습니다.
                        |  /키카드 등록 [이름]: 손에 들고 있는 아이템이 해당 권한의 키카드가 됩니다.
                        """.trimIndent().also {
                            component(it).send(player)
                        }
                    }
                }
                then("생성") {
                    executes { player.sendMessage("명령어 사용법: /키카드 help"); }
                    then("args" to string(GREEDY_PHRASE)) {
                        executes {
                            val args: String by it

                            if(args.split(" ").size < 2){
                                player.sendMessage("불완전한 명령어입니다: /키카드 help")
                                return@executes
                            }

                            val name: String = args.split(" ")[0]
                            val tag: String = args.split(" ")[1]

                            transaction {
                                try {
                                    if (KeyCard.isExistsKeyCard(name, tag)) {
                                        player.sendMessage("이미 등록되어있는 키카드입니다.")
                                    } else {
                                        if(Role.isExistsRole(tag)){
                                            KeyCard.new {
                                                this.name = name
                                                this.role = Role.find { Roles.role eq tag }.first().id
                                            }
                                            player.sendMessage("키카드 이름: $name 태그: $tag 생성됨.")
                                        } else player.sendMessage("태그: $tag 가 등록되어 있지 않습니다.")
                                    }
                                } catch (e: SQLException) {
                                    player.sendMessage(e.message!!)
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

                            transaction {
                                val itemInHand = player.inventory.itemInMainHand
                                if (itemInHand.type != Material.IRON_DOOR) {
                                    player.sendMessage("철문을 손에 들고 명령어를 입력해 주세요.")
                                    return@transaction
                                }

                                if(KeyCard.isExistsKeyName(name)) {
                                    val keyCardDoorItem = ItemStackBuilder()
                                        .setType(itemInHand.type)
                                        .setDisplayName(component(name, NamedTextColor.BLUE)
                                            .append(component(" 철문", NamedTextColor.WHITE)))
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

                            transaction {
                                var itemInHand = player.inventory.itemInMainHand
                                if (itemInHand.type == Material.IRON_DOOR) return@transaction
                                if (itemInHand.isEmpty) {
                                    player.sendMessage("아이템을 손에 들고 명령어를 입력해 주세요.")
                                    return@transaction
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
            }
        }
    }
}