package io.github.hxxniverse.hobeaktown.feature.coupon

import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CouponCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("coupon") {
                executes {
                    AnvilInventory(
                        title = "쿠폰 사용",
                        text = "쿠폰 이름을 입력하세요.",
                        onClickResult = { state ->
                            transaction {
                                val coupon = Coupon.find { Coupons.name eq state.text }.firstOrNull()

                                if (coupon == null) {
                                    return@transaction listOf(AnvilGUI.ResponseAction.replaceInputText("쿠폰이 존재하지 않습니다."))
                                }

                                if (!player.user.canUseCoupon(coupon)) {
                                    return@transaction listOf(AnvilGUI.ResponseAction.replaceInputText("이미 수령하셨습니다."))
                                }

                                return@transaction transaction {
                                    player.user.useCoupon(coupon)
                                    player.sendMessage("쿠폰을 수령하였습니다.")
                                    listOf(AnvilGUI.ResponseAction.close())
                                }
                            }
                        }
                    ).open(player)
                }
                then("create") {
                    then("name" to string(StringType.QUOTABLE_PHRASE)) {
                        then("expireHour" to int()) {
                            executes {
                                transaction {
                                    val name: String by it
                                    val expireHour: Int by it

                                    Coupon.new {
                                        this.name = name
                                        this.expiredDate = LocalDateTime.now().plusHours(expireHour.toLong())
                                    }

                                    sender.sendMessage("쿠폰이 생성되었습니다.")
                                }
                            }
                        }
                    }
                }
                then("set-item") {
                    then("coupon" to coupon()) {
                        executes {
                            transaction {
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
    transaction {
        Coupon.find { Coupons.name eq input }.firstOrNull()
    }
}.apply {
    suggests {
        transaction {
            suggest(
                candidates = Coupon.find { Coupons.name like "$it%" }.map { it.name },
                tooltip = { it.component() },
            )
        }
    }
}