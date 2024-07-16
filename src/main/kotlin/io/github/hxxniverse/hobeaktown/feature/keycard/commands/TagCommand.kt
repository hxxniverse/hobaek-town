package io.github.hxxniverse.hobeaktown.feature.keycard.commands

import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Role
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.UserKeyCard
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import java.sql.SQLException

class TagCommand() : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("태그") {
                requires { sender is Player }
                executes { player.sendMessage("명령어 도우미: /키카드 help") }
                then("help") {
                    executes {
                        """
                        |태그 명령어
                        |  /태그 리스트: 현재 등록되어있는 태그의 전체 리스트를 출력합니다.
                        |  /태그 생성 [역할]: 새로운 태그(역할)을 등록합니다.
                        |  /태그 등록 [역할]: 역할 지정 아이템이 주어집니다. 해당 아이템으로 플레이어를 우클릭 시 플레이어의 역할이 변경됩니다.
                        |  /태그 등록취소 [역할]: 역할 취소 아이템이 주어집니다. 해당 아이템으로 플레이어를 우클릭 시 플레이어의 해당 역할이 취소됩니다.
                        |  */태그 설정 [플레이어] [역할]: 플레이어의 역할을 강제로 변경시킵니다.
                        """.trimIndent().also {
                            component(it).send(player)
                        }
                    }
                }
                then("리스트"){
                    executes {
                        transaction {
                            Role.all().forEach { player.sendMessage("${it.role}") }
                        }
                    }
                }
                then("생성") {
                    executes { player.sendMessage("명령어 사용법: /태그 help") }
                    then("role" to string(StringType.GREEDY_PHRASE)) {
                        executes {
                            val role: String by it

                            player.sendMessage("입력된 역할: $role")

                            loggedTransaction {
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
                    executes { player.sendMessage("명령어 사용법: /태그 help") }
                    then("role" to string(StringType.GREEDY_PHRASE)) {
                        executes {
                            val role: String by it
                            if(Role.isExistsRole(role)){
                                val netherStar = ItemStackBuilder()
                                    .setType(Material.NETHER_STAR)
                                    .setDisplayName(
                                        component(role, NamedTextColor.BLUE)
                                            .append(component(" 역할 등록", NamedTextColor.WHITE)))
                                    .addPersistentData("RoleRegister", "true")
                                    .addPersistentData("Role", role)
                                    .build()
                                player.inventory.setItemInMainHand(netherStar)
                                player.sendMessage("'$role' 역할 등록 아이템이 지급되었습니다.")
                            } else player.sendMessage(
                                component(role, NamedTextColor.RED)
                                    .append(component(" 역할이 등록되어 있지 않습니다.")))
                        }
                    }
                }
                then("등록취소"){
                    executes { player.sendMessage("명령어 사용법: /태그 help") }
                    then("role" to string(StringType.GREEDY_PHRASE)){
                        executes {
                            val role: String by it

                            if(Role.isExistsRole(role)){
                                val blazeRod = ItemStackBuilder()
                                    .setType(Material.BLAZE_ROD)
                                    .setDisplayName(
                                        component(role, NamedTextColor.RED)
                                            .append(component(" 역할 취소", NamedTextColor.WHITE)))
                                    .addPersistentData("RoleRegister", "true")
                                    .addPersistentData("Role", role)
                                    .build()
                                player.inventory.setItemInMainHand(blazeRod)
                                player.sendMessage("'$role' 역할 취소 아이템이 지급되었습니다.")
                            } else player.sendMessage(
                                component(role, NamedTextColor.RED)
                                    .append(component(" 역할이 등록되어 있지 않습니다.")))
                        }
                    }
                }
                then("설정"){
                    requires { player.isOp }
                    executes { player.sendMessage("명령어 사용법: /태그 help") }
                    then("player" to player()){
                        executes { player.sendMessage("명령어 사용법: /태그 help") }
                        then("role" to string(StringType.GREEDY_PHRASE)){
                            executes {
                                val player: Player by it
                                val role: String by it

                                loggedTransaction {
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
