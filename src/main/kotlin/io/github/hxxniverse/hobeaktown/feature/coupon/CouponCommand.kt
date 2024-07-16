package io.github.hxxniverse.hobeaktown.feature.coupon

import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.command_help.help
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.plugin.java.JavaPlugin
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage
import java.time.LocalDateTime

class CouponCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("coupon") {
                then("도움말") {
                    executes {
                        help("쿠폰") {
                            command("쿠폰") {
                                description = "쿠폰 입력하는 창을 엽니다."
                            }
                            command("생성 <name> <expireHour>") {
                                description = "쿠폰을 생성합니다."
                            }
                            command("아이템설정 <coupon>") {
                                description = "쿠폰 보상 아이템을 설정합니다."
                            }
                        }
                    }
                }
                executes {
                    AnvilInventory(
                        title = "쿠폰 사용",
                        text = "쿠폰 이름을 입력하세요.",
                        onClickResult = { state ->
                            loggedTransaction {
                                val coupon = Coupon.find { Coupons.name eq state.text }.firstOrNull()

                                if (coupon == null) {
                                    return@loggedTransaction listOf(AnvilGUI.ResponseAction.replaceInputText("쿠폰이 존재하지 않습니다."))
                                }

                                if (!player.user.canUseCoupon(coupon)) {
                                    return@loggedTransaction listOf(AnvilGUI.ResponseAction.replaceInputText("이미 수령하셨습니다."))
                                }

                                return@loggedTransaction loggedTransaction {
                                    player.user.useCoupon(coupon)
                                    player.sendInfoMessage("쿠폰을 수령하였습니다.")
                                    listOf(AnvilGUI.ResponseAction.close())
                                }
                            }
                        }
                    ).open(player)
                }
                then("생성") {
                    then("name" to string(StringType.QUOTABLE_PHRASE)) {
                        then("expireHour" to int()) {
                            executes {
                                loggedTransaction {
                                    val name: String by it
                                    val expireHour: Int by it

                                    Coupon.new {
                                        this.name = name
                                        this.expiredDate = LocalDateTime.now().plusHours(expireHour.toLong())
                                    }

                                    sender.sendInfoMessage("쿠폰이 생성되었습니다.")
                                }
                            }
                        }
                    }
                }
                then("아이템설정") {
                    then("coupon" to coupon()) {
                        executes {
                            loggedTransaction {
                                val coupon: Coupon by it

                                CouponItemSetUi(coupon).open(player)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun coupon() = KommandArgument.dynamic(type = StringType.QUOTABLE_PHRASE) { _, input ->
    loggedTransaction {
        Coupon.find { Coupons.name eq input }.firstOrNull()
    }
}.apply {
    suggests {
        loggedTransaction {
            suggest(
                candidates = Coupon.find { Coupons.name like "$it%" }.map { it.name },
                tooltip = { it.component() },
            )
        }
    }
}