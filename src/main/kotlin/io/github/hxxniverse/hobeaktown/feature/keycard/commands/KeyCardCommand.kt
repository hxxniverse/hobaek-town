package io.github.hxxniverse.hobeaktown.feature.keycard.commands

import io.github.monun.kommand.StringType.GREEDY_PHRASE;
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.KeyCard
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Role
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Roles
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

class KeyCardCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("키카드") {
                requires {
                    sender is Player
                }
                executes {
                    player.sendMessage("키카드 관련 명령어: /키카드 [생성|철문|등록|아이템설정]")
                }
                then("생성") {
                    executes {
                        player.sendMessage("키카드 명령어: /키카드 생성 <이름> <태그>");
                    }
                    then("args" to string(GREEDY_PHRASE)) {
                        executes {
                            val args: String by it
                            val name: String = args.split(" ")[0]
                            val tag: String = args.split(" ")[1]

                            if(name.isEmpty() || tag.isEmpty()){
                                player.sendMessage("불완전한 명령어입니다.")
                                return@executes
                            }

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
                                            player.sendMessage("키카드 $name 태그: $tag 생성됨.")
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
                    executes {
                        player.sendMessage("키카드 명령어: /키카드 철문 <이름>");
                    }
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
                                        .setDisplayName(text(name, NamedTextColor.BLUE).append(text(" 철문", NamedTextColor.WHITE)))
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
                    executes {
                        player.sendMessage("키카드 명령어: /키카드 등록 <이름>");
                    }
                    then("name" to string(GREEDY_PHRASE)) {
                        executes {
                            val name: String by it

                            transaction {
                                var itemInHand = player.inventory.itemInMainHand
                                if(itemInHand.type == Material.IRON_DOOR) return@transaction
                                if (itemInHand.isEmpty) {
                                    player.sendMessage("아이템을 손에 들고 명령어를 입력해 주세요.")
                                    return@transaction
                                }

                                if(KeyCard.isExistsKeyName(name)) {
                                    itemInHand = ItemStackBuilder()
                                        .setType(itemInHand.type)
                                        .setDisplayName(text(name, NamedTextColor.BLUE).append(text(" 키카드", NamedTextColor.WHITE)))
                                        .addPersistentData("NameRegister", "true")
                                        .addPersistentData("Name", name)
                                        .build()

                                    player.inventory.setItemInMainHand(itemInHand)
                                    player.sendMessage("$name 키카드 등록됨.")
                                } else player.sendMessage("'$name'은 등록되지 않은 이름입니다.")
                            }
                            }
                        }
                }
            }
        }
    }
}