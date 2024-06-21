package io.github.hxxniverse.hobeaktown.feature.keycard.commands

import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Role
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.UserKeyCard
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException
import java.util.*

class TagCommand() : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("태그") {
                requires {
                    sender is Player
                }
                executes {
                    player.sendMessage("태그 관련 명령어: /태그 [생성|등록|등록취소|설정]")
                }
                then("생성") {
                    executes {
                        player.sendMessage("명령어 사용법: /태그 생성 <역할>")
                    }
                    then("role" to string(StringType.GREEDY_PHRASE)) {
                        executes {
                            val role: String by it

                            player.sendMessage("입력된 역할: $role")

                            transaction {
                                try {
                                    if (!Role.isExistsRole(role)) {
                                        Role.new {
                                            this.role = role
                                        }
                                        player.sendMessage("역할 '$role'이(가) 생성되었습니다.")
                                    } else {
                                        player.sendMessage("역할 '$role'이(가) 이미 존재합니다.")
                                    }
                                } catch (e: SQLException) {
                                    player.sendMessage("역할 생성 중 오류가 발생했습니다: ${e.message}")
                                }
                            }
                        }
                    }
                }
                then("등록") {
                    executes {
                        player.sendMessage("명령어 사용법: /태그 등록 <역할>")
                    }
                    then("role" to string(StringType.GREEDY_PHRASE)) {
                        executes {
                            val role: String by it
                            if(Role.isExistsRole(role)){
                                val netherStar = ItemStackBuilder()
                                    .setType(Material.NETHER_STAR)
                                    .setDisplayName(text(role, NamedTextColor.BLUE).append(text(" 역할 등록", NamedTextColor.WHITE)))
                                    .addPersistentData("RoleRegister", "true")
                                    .addPersistentData("Role", role)
                                    .build()
                                player.inventory.setItemInMainHand(netherStar)
                                player.sendMessage("'$role' 역할 등록 아이템이 지급되었습니다.")
                            } else player.sendMessage(text(role, NamedTextColor.RED).append(text(" 역할이 등록되어 있지 않습니다.")))
                        }
                    }
                }
                then("등록취소"){
                    then("role" to string(StringType.GREEDY_PHRASE)){
                        executes {
                            val role: String by it

                            if(Role.isExistsRole(role)){
                                val blazeRod = ItemStackBuilder()
                                    .setType(Material.BLAZE_ROD)
                                    .setDisplayName(text(role, NamedTextColor.RED).append(text(" 역할 취소", NamedTextColor.WHITE)))
                                    .addPersistentData("RoleRegister", "true")
                                    .addPersistentData("Role", role)
                                    .build()
                                player.inventory.setItemInMainHand(blazeRod)
                                player.sendMessage("'$role' 역할 취소 아이템이 지급되었습니다.")
                            } else player.sendMessage(text(role, NamedTextColor.RED).append(text(" 역할이 등록되어 있지 않습니다.")))
                        }
                    }
                }
                then("설정"){
                    executes {
                        player.sendMessage("명령어 사용법: /태그 설정 <플레이어> <역할>")
                    }
                    then("player" to player()){
                        then("role" to string(StringType.GREEDY_PHRASE)){
                            executes {
                                val player: Player by it
                                val role: String by it

                                transaction {
                                    try {
                                        UserKeyCard.updateMemberRole(player.uniqueId, role);
                                        player.sendMessage("플레이어의 역할이 변경되었습니다.")
                                    } catch (e : SQLException) {
                                        player.sendMessage("역할 변경에 실패했습니다.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
