package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.feature.real_estate.ui.RealEstateUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.emptyLocation
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class RealEstateCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("real-estate") {
                executes {
                    RealEstateUi().open(player)
                }
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
                        |  /real-estate land_appraisal_certificate : 토지 감정증명서를 생성합니다.
                        |  /real-estate real_estate_certificate : 부동산 등기증을 생성합니다.
                        |  /real-estate scheme : 부동산 스키마를 생성합니다.
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
                            val realEstate =
                                RealEstate.find { RealEstates.signLocation eq targetBlockLocation }.firstOrNull()

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
                then("land_appraisal_certificate") {
                    executes {
                        transaction {
                            val certificate = RealEstatesItem.LAND_APPRAISAL_CERTIFICATE
                            player.inventory.addItem(certificate)
                        }
                    }
                }
                then("real_estate_certification") {
                    executes {
                        transaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }
                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@transaction
                            }
                            val certificate = RealEstatesItem.REAL_ESTATE_CERTIFICATE(realEstate)
                            player.inventory.addItem(certificate)
                        }
                    }
                }
                then("scheme") {
                    then("type" to string()) {
                        executes {
                            transaction {
                                val type: String by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@transaction
                                }

                                if (type == "save") {
                                    realEstate.saveScheme()
                                    text("부동산 스키마를 성공적으로 생성하였습니다.").send(player)
                                } else if (type == "load") {
                                    realEstate.loadScheme()
                                    text("부동산 스키마를 성공적으로 불러왔습니다.").send(player)
                                }
                            }
                        }
                    }
                }
                then("sell") {
                    executes {
                        transaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@transaction
                            }

                            realEstate.sell(player)
                            text("부동산을 성공적으로 판매하였습니다.").send(player)
                        }
                    }
                }
                then("transfer") {
                    then("player" to player()) {
                        executes {
                            transaction {
                                val target: Player by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@transaction
                                }

                                realEstate.owner = target.uniqueId
                                text("부동산을 성공적으로 양도하였습니다.").send(player)
                            }
                        }
                    }
                }
                then("grade") {
                    then("grade" to dynamicByEnum(EnumSet.allOf(RealEstateGrade::class.java))) {
                        executes {
                            transaction {
                                val grade: RealEstateGrade by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@transaction
                                }

                                realEstate.grade = grade
                                realEstate.updateSign()
                                text("부동산을 성공적으로 등급을 변경하였습니다.").send(player)
                            }
                        }
                    }
                }
                then("clear") {
                    executes {
                        transaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@transaction
                            }

                            realEstate.clean()
                            text("부동산을 성공적으로 청소하였습니다.").send(player)
                        }
                    }
                }
                then("members") {
                    executes {
                        transaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@transaction
                            }

                            realEstate.listMembers(player)
                        }
                    }
                }
                then("boundary") {
                    executes {
                        transaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@transaction
                            }

                            text("부동산 경계").send(player)
                            text("pos1: ${realEstate.pos1}").send(player)
                            text("pos2: ${realEstate.pos2}").send(player)
                        }
                    }
                }
                then("invite") {
                    then("target" to player()) {
                        executes {
                            transaction {
                                val target: Player by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@transaction
                                }

                                realEstate.invite(player, target)
                                text("부동산을 성공적으로 초대하였습니다.").send(player)
                            }
                        }
                    }
                }
                then("kick") {
                    then("target" to player()) {
                        executes {
                            transaction {
                                val target: Player by it
                                val realEstate = RealEstate.all().find { it.isInside(target.location) }

                                if (realEstate == null) {
                                    text("해당 부지에 부동산이 존재하지 않습니다.").send(target)
                                    return@transaction
                                }

                                realEstate.kick(player, target)
                                text("부동산을 성공적으로 추방하였습니다.").send(target)
                            }
                        }
                    }
                }
                then("extend") {
                    then("price" to int()) {
                        executes {
                            transaction {
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@transaction
                                }

                                if (realEstate.owner != player.uniqueId) {
                                    text("부동산의 소유자만 부동산을 연장할 수 있습니다.").send(player)
                                    return@transaction
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
                                transaction {
                                    val player: Player by it
                                    val price: Int by it
                                    val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                    if (realEstate == null) {
                                        text("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                        return@transaction
                                    }

                                    realEstate.rent(player, player, price)
                                    text("부동산을 성공적으로 대여하였습니다.").send(player)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}