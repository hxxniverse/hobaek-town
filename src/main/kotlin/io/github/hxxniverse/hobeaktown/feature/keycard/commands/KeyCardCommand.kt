package io.github.hxxniverse.hobeaktown.feature.keycard.commands

import io.github.hxxniverse.hobeaktown.feature.keycard.entity.KeyCard
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Role
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Roles
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
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
                    then("name" to string()) {
                        then("tag" to string()) {
                            executes {
                                val name: String by it
                                val tag: String by it

                                transaction {
                                    try {
                                        if (KeyCard.isExistsKeyCard(name, tag)) {
                                            player.sendMessage("이미 등록되어있는 키카드입니다.")
                                        } else {
                                            KeyCard.new {
                                                this.name = name
                                                this.role = Role.find { Roles.name eq tag }.first().id
                                            }
                                            player.sendMessage("키카드 $name (태그: $tag) 생성됨.")
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
                    executes {
                        player.sendMessage("키카드 명령어: /키카드 철문 <이름>");
                    }
                    then("name" to string()) {
                        executes {
                            val name: String by it

                            transaction {
                                val itemInHand = player.inventory.itemInMainHand
                                if (itemInHand.type != Material.IRON_DOOR) {
                                    player.sendMessage("철문을 손에 들고 명령어를 입력해 주세요.")
                                    return@transaction
                                }
                                val keyCardDoorItem = itemInHand.edit {
                                    setDisplayName("$name 문")
                                    setEnchantmentStorageMeta {
                                        addStoredEnchant(Enchantment.LUCK, 1, true)
                                        addItemFlags(ItemFlag.HIDE_ENCHANTS)
                                    }
                                    addPersistentData("PermissionRegister", "true")
                                    addPersistentData("Permission", name)
                                }
                                player.inventory.setItemInMainHand(keyCardDoorItem)
                                player.sendMessage("철문에 $name 권한 등록됨.")
                            }
                        }
                    }
                }
                then("등록") {
                    executes {
                        player.sendMessage("키카드 명령어: /키카드 등록 <이름>");
                    }
                    then("name" to string()) {
                        executes {
                            val name: String by it

                            transaction {
                                val itemInHand = player.inventory.itemInMainHand
                                if (itemInHand.type != Material.IRON_DOOR) {
                                    player.sendMessage("아이템을 손에 들고 명령어를 입력해 주세요.")
                                    return@transaction
                                }
                                val keyCardDoorItem = itemInHand.edit {
                                    setDisplayName("$name 키카드")
                                    setEnchantmentStorageMeta {
                                        addStoredEnchant(Enchantment.LUCK, 1, true)
                                        addItemFlags(ItemFlag.HIDE_ENCHANTS)
                                    }
                                    addPersistentData("PermissionRegister", "true")
                                    addPersistentData("Permission", name)
                                }
                                player.inventory.setItemInMainHand(keyCardDoorItem)
                                player.sendMessage("철문에 $name 권한 등록됨.")
                            }
                        }
                    }
                }
            }
        }
    }
}