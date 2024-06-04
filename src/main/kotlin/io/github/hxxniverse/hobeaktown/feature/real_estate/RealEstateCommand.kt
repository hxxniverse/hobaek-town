package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.emptyLocation
import io.github.hxxniverse.hobeaktown.util.extension.setPersistentData
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * /부동산 생성 (일반/토지) (이름) (가격) (사용 가능 날짜) ex) /부동산 생성 일반 암남1동 30000 0
 *
 * /부동산 판매 - 등급이 달린 토지를 팔아도 원래 구매가격을 돌려줍니다.
 *
 * /부동산 양도 (유저 이름) - 등급이 달린 그대로 양도할 수 있습니다.
 *
 * /부동산 등급 설정 - op명령어로 해단 부지에 가서 명령어 입력시 등급을 바꾸어줄 수 있습니다.
 *
 * /부동산 청소 - 해당 부지에 있는 모든 블럭을 제거하고 원상태로 만듭니다.
 *                    제거한 블럭과 다른 자제는 모두 가운데 상자에 들어갑니다.
 *
 * /부동산 목록 - 자신이 가지고 있는 부동산을 확인 할 수 있습니다.
 *                    채팅으로 (이름) (위치 - 좌표) (남은 기간)이 뜹니다.
 *
 * /부동산 경계 - 현재 서 있는 자신의 부동산의 경계를 확인 할 수 있습니다.
 *                    경계 내부에서는 블럭을 부수고 설치할 수 있습니다.
 *
 * /부동산 - 부동산 관련된 GUI가 뜹니다.
 * /부동산 초대 (플레리어 이름) - 자신이 소유한, 임대한 부동산에 플레이어를 초대합니다.
 *                    초대된 플레이어는 해당 부동산의 소유자와 같은 권한을 받아
 *                    부동산내의 블럭을 설치, 파괴 할 수 있습니다.
 *
 * /부동산 추방 (플레이어 이름) - 자신이 소유한, 임대한 부동산에 플레이어를 추방합니다.
 *                    추방시 추방된 플레이어가 설치한 상자는
 *
 * "/부동산 초대 (플레리어 이름) - 자신이 소유한, 임대한 부동산에 플레이어를 초대합니다.
 *                  초대된 플레이어는 해당 부동산의 소유자와 같은 권한을 받아
 *                  부동산내의 블럭을 설치, 파괴 할 수 있습니다.
 *
 * /부동산 추방 (플레이어 이름) - 자신이 소유한, 임대한 부동산에 플레이어를 추방합니다.
 *                  추방시 추방된 플레이어가 설치한 상자는 열 수 없습니다.
 *
 * /부동산 대여 (초대 츨레이어 이름) (하루에 내야 하는 돈) - 24시간 뒤에 초대한 플레이어에게 돈이 입금됩니다.
 * 만약 초대 받은 사람의 잔액이 부족해서 입금되지 않으면
 * 초대한 플레이어에게 [""플레이어 이름"" 돈이 입금되지 않았습니다] 라고 뜹니다.  "
 */
class RealEstateCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("real-estate") {
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
                then("판매") {
                    executes {
                        // TODO
                    }
                }
                then("양도") {
                    then("유저 이름") {
                        executes {
                            // TODO
                        }
                    }
                }
                then("등급 설정") {
                    executes {
                        // TODO
                    }
                }
                then("청소") {
                    executes {
                        // TODO
                    }
                }
                then("목록") {
                    executes {
                        // TODO
                    }
                }
                then("경계") {
                    executes {
                        // TODO
                    }
                }
                then("초대") {
                    then("플레리어 이름") {
                        executes {
                            // TODO
                        }
                    }
                }
                then("추방") {
                    then("플레이어 이름") {
                        executes {
                            // TODO
                        }
                    }
                }
                then("대여") {
                    then("초대 츨레이어 이름") {
                        then("하루에 내야 하는 돈") {
                            executes {
                                // TODO
                            }
                        }
                    }
                }
            }
        }
    }
}