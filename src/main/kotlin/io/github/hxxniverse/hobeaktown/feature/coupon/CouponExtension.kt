package io.github.hxxniverse.hobeaktown.feature.coupon

import io.github.hxxniverse.hobeaktown.feature.user.User
import org.jetbrains.exposed.sql.and
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage

fun User.useCoupon(coupon: Coupon) = loggedTransaction {
    CouponUsage.new {
        this.coupon = coupon
        this.user = this@useCoupon
    }

    coupon.items.forEach {
        player?.inventory?.addItem(it.item)
    }

    player?.sendInfoMessage("쿠폰의 아이템이 지급되었습니다.")
}

fun User.canUseCoupon(coupon: Coupon): Boolean = loggedTransaction {
    return@loggedTransaction CouponUsage.find { CouponUsages.user eq this@canUseCoupon.id and (CouponUsages.coupon eq coupon.id) }.empty()
}