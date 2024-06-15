package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.emptyLocation
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.hxxniverse.hobeaktown.util.extension.setPersistentData
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class RealEstateCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("real-estate") {
                then("help") {
                    executes {
                        """
                        |부동산 명령어
                        |  /real-estate sell : 부동산을 판매합니다.
                        |  /real-estate transfer <player> : 부동산을 양도합니다.
                        |  /real-estate clear : 부동산을 청소합니다.
                        |  /real-estate members : 부동산 멤버를 확인합니다.
                        |  /real-estate boundary : 부동산 경계를 확인합니다.
                        |  /real-estate invite <player> : 부동산을 초대합니다.
                        |  /real-estate kick <player> : 부동산을 추방합니다.
                        |  /real-estate extend <player> <price> : 부동산을 연장합니다.
                        |  /real-estate rent <player> <price> : 부동산을 대여합니다.
                        |부동산 관리자 명령어
                        |  /real-estate create <type> <name> <price> <due> : 부동산을 생성합니다.
                        |  /real-estate grade <grade> : 부동산 등급을 변경합니다.
                        """.trimIndent().also {
                            text(it).send(player)
                        }
                    }
                }
                then("target-update") {
                    executes {
                        transaction {
                            val targetBlockLocation: Location = player.getTargetBlockExact(100)!!.location
                            val realEstate = RealEstate.find { RealEstates.signLocation eq targetBlockLocation }.firstOrNull()

                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@transaction
                            }

                            realEstate.updateSign()
                            text("부동산을 성공적으로 갱신하였습니다.").send(player)
                        }
                    }
                }
                then("create") {
                    then("type" to dynamicByEnum(EnumSet.allOf(RealEstateType::class.java))) {
                        then("name" to string(type = StringType.QUOTABLE_PHRASE)) {
                            then("price" to int()) {
                                then("due" to int()) {
                                    executes {
                                        val type: RealEstateType by it
                                        val name: String by it
                                        val price: Int by it
                                        val due: Int by it

                                        // Create RealEstateSelection
                                        val selection = RealEstateSelection(
                                            name = name,
                                            price = price,
                                            due = due,
                                            type = type,
                                            pos1 = emptyLocation(),
                                            pos2 = emptyLocation()
                                        )

                                        player.inventory.addItem(selection.toItemStack())
                                    }
                                }
                            }
                        }
                    }
                }
                then("sell") {
                    executes {
                        val realEstate = RealEstate.all().find { it.isInside(player.location) }

                        if (realEstate == null) {
                            text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                            return@executes
                        }

                        realEstate.sell(player)
                        text("부동산을 성공적으로 판매하였습니다.").send(player)
                    }
                }
                then("transfer") {
                    then("player" to player()) {
                        executes {
                            val target: Player by it
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@executes
                            }

                            realEstate.owner = target.uniqueId
                            text("부동산을 성공적으로 양도하였습니다.").send(player)
                        }
                    }
                }
                then("grade") {
                    then("grade" to dynamicByEnum(EnumSet.allOf(RealEstateGrade::class.java))) {
                        executes {
                            val grade: RealEstateGrade by it
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@executes
                            }

                            realEstate.grade = grade
                            text("부동산을 성공적으로 등급을 변경하였습니다.").send(player)
                        }
                    }
                }
                then("clear") {
                    executes {
                        val realEstate = RealEstate.all().find { it.isInside(player.location) }

                        if (realEstate == null) {
                            text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                            return@executes
                        }

                        realEstate.clean()
                        text("부동산을 성공적으로 청소하였습니다.").send(player)
                    }
                }
                then("members") {
                    executes {
                        val realEstate = RealEstate.all().find { it.isInside(player.location) }

                        if (realEstate == null) {
                            text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                            return@executes
                        }

                        realEstate.listMembers(player)
                    }
                }
                then("boundary") {
                    executes {
                        val realEstate = RealEstate.all().find { it.isInside(player.location) }

                        if (realEstate == null) {
                            text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                            return@executes
                        }

                        text("부동산 경계").send(player)
                        text("pos1: ${realEstate.pos1}").send(player)
                        text("pos2: ${realEstate.pos2}").send(player)
                    }
                }
                then("invite") {
                    then("player" to player()) {
                        executes {
                            val target: Player by it
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@executes
                            }

                            realEstate.invite(player, target)
                            text("부동산을 성공적으로 초대하였습니다.").send(player)
                        }
                    }
                }
                then("kick") {
                    then("player" to player()) {
                        executes {
                            val target: Player by it
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@executes
                            }

                            realEstate.kick(player, target)
                            text("부동산을 성공적으로 추방하였습니다.").send(player)
                        }
                    }
                }
                then("extend") {
                    then("player" to player()) {
                        then("price" to int()) {
                            executes {
                                val target: Player by it
                                val price: Int by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@executes
                                }

                                realEstate.extend(player)
                                text("부동산을 성공적으로 연장하였습니다.").send(player)
                            }
                        }
                    }
                }
                then("rent") {
                    then("player" to player()) {
                        then("price" to int()) {
                            executes {
                                val target: Player by it
                                val price: Int by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@executes
                                }

                                realEstate.rent(player, target, price)
                                text("부동산을 성공적으로 대여하였습니다.").send(player)
                            }
                        }
                    }
                }
            }
        }
    }
}