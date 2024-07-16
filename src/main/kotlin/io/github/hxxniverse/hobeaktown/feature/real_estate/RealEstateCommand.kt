package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.feature.real_estate.ui.RealEstateUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.command_help.help
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.emptyLocation
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
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
                        help("real-estate") {
                            command("real-estate") {
                                description = "부동산 UI 열기"
                            }
                            command("real-estate create <type> <name> <price> <due>") {
                                description = "부동산 생성"
                            }
                            command("real-estate land_appraisal_certificate") {
                                description = "토지 감정증서"
                            }
                            command("real-estate real_estate_certification") {
                                description = "부동산 등기증"
                            }
                            command("real-estate scheme <type>") {
                                description = "부동산 스키마 생성/불러오기"
                            }
                            command("real-estate sell") {
                                description = "부동산 판매"
                            }
                            command("real-estate transfer <player>") {
                                description = "부동산 양도"
                            }
                            command("real-estate grade <grade>") {
                                description = "부동산 등급 변경"
                            }
                            command("real-estate clear") {
                                description = "부동산 청소"
                            }
                            command("real-estate members") {
                                description = "부동산 멤버 목록"
                            }
                            command("real-estate boundary") {
                                description = "부동산 경계"
                            }
                            command("real-estate invite <target>") {
                                description = "부동산 초대"
                            }
                            command("real-estate kick <target>") {
                                description = "부동산 추방"
                            }
                            command("real-estate extend <price>") {
                                description = "부동산 연장"
                            }
                            command("real-estate rent <player> <price>") {
                                description = "부동산 대여"
                            }
                            command("real-estate target-update") {
                                description = "부동산 갱신"
                            }
                        }
                    }
                }
                then("target-update") {
                    executes {
                        loggedTransaction {
                            val targetBlockLocation: Location = player.getTargetBlockExact(100)!!.location
                            val realEstate =
                                RealEstate.find { RealEstates.signLocation eq targetBlockLocation }.firstOrNull()

                            if (realEstate == null) {
                                component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@loggedTransaction
                            }

                            realEstate.updateSign()
                            component("부동산을 성공적으로 갱신하였습니다.").send(player)
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
                        loggedTransaction {
                            val certificate = RealEstatesItem.LAND_APPRAISAL_CERTIFICATE
                            player.inventory.addItem(certificate)
                        }
                    }
                }
                then("real_estate_certification") {
                    executes {
                        loggedTransaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }
                            if (realEstate == null) {
                                component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@loggedTransaction
                            }
                            val certificate = RealEstatesItem.REAL_ESTATE_CERTIFICATE(realEstate)
                            player.inventory.addItem(certificate)
                        }
                    }
                }
                then("scheme") {
                    then("type" to string()) {
                        executes {
                            loggedTransaction {
                                val type: String by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@loggedTransaction
                                }

                                if (type == "save") {
                                    realEstate.saveScheme()
                                    component("부동산 스키마를 성공적으로 생성하였습니다.").send(player)
                                } else if (type == "load") {
                                    realEstate.loadScheme()
                                    component("부동산 스키마를 성공적으로 불러왔습니다.").send(player)
                                }
                            }
                        }
                    }
                }
                then("sell") {
                    executes {
                        loggedTransaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@loggedTransaction
                            }

                            realEstate.sell(player)
                            component("부동산을 성공적으로 판매하였습니다.").send(player)
                        }
                    }
                }
                then("transfer") {
                    then("player" to player()) {
                        executes {
                            loggedTransaction {
                                val target: Player by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@loggedTransaction
                                }

                                realEstate.owner = target.uniqueId
                                component("부동산을 성공적으로 양도하였습니다.").send(player)
                            }
                        }
                    }
                }
                then("grade") {
                    then("grade" to dynamicByEnum(EnumSet.allOf(RealEstateGrade::class.java))) {
                        executes {
                            loggedTransaction {
                                val grade: RealEstateGrade by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@loggedTransaction
                                }

                                realEstate.grade = grade
                                realEstate.updateSign()
                                component("부동산을 성공적으로 등급을 변경하였습니다.").send(player)
                            }
                        }
                    }
                }
                then("clear") {
                    executes {
                        loggedTransaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@loggedTransaction
                            }

                            realEstate.clean()
                            component("부동산을 성공적으로 청소하였습니다.").send(player)
                        }
                    }
                }
                then("members") {
                    executes {
                        loggedTransaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@loggedTransaction
                            }

                            realEstate.listMembers(player)
                        }
                    }
                }
                then("boundary") {
                    executes {
                        loggedTransaction {
                            val realEstate = RealEstate.all().find { it.isInside(player.location) }

                            if (realEstate == null) {
                                component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                return@loggedTransaction
                            }

                            component("부동산 경계").send(player)
                            component("pos1: ${realEstate.pos1}").send(player)
                            component("pos2: ${realEstate.pos2}").send(player)
                        }
                    }
                }
                then("invite") {
                    then("target" to player()) {
                        executes {
                            loggedTransaction {
                                val target: Player by it
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@loggedTransaction
                                }

                                realEstate.invite(player, target)
                                component("부동산을 성공적으로 초대하였습니다.").send(player)
                            }
                        }
                    }
                }
                then("kick") {
                    then("target" to player()) {
                        executes {
                            loggedTransaction {
                                val target: Player by it
                                val realEstate = RealEstate.all().find { it.isInside(target.location) }

                                if (realEstate == null) {
                                    component("해당 부지에 부동산이 존재하지 않습니다.").send(target)
                                    return@loggedTransaction
                                }

                                realEstate.kick(player, target)
                                component("부동산을 성공적으로 추방하였습니다.").send(target)
                            }
                        }
                    }
                }
                then("extend") {
                    then("price" to int()) {
                        executes {
                            loggedTransaction {
                                val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                if (realEstate == null) {
                                    component("해당 부지에 부동산이 존재하지 않습니다.").send(player)
                                    return@loggedTransaction
                                }

                                if (realEstate.owner != player.uniqueId) {
                                    component("부동산의 소유자만 부동산을 연장할 수 있습니다.")
                                        .send(player)
                                    return@loggedTransaction
                                }

                                realEstate.extend(player)
                                component("부동산을 성공적으로 연장하였습니다.").send(player)
                            }
                        }
                    }
                }
                then("rent") {
                    then("player" to player()) {
                        then("price" to int()) {
                            executes {
                                loggedTransaction {
                                    val player: Player by it
                                    val price: Int by it
                                    val realEstate = RealEstate.all().find { it.isInside(player.location) }

                                    if (realEstate == null) {
                                        component("해당 부지에 부동산이 존재하지 않습니다.")
                                            .send(player)
                                        return@loggedTransaction
                                    }

                                    realEstate.rent(player, player, price)
                                    component("부동산을 성공적으로 대여하였습니다.").send(player)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}