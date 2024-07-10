package io.github.hxxniverse.hobeaktown.feature.coupon

import io.github.hxxniverse.hobeaktown.feature.user.User
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

fun User.useCoupon(coupon: Coupon) = transaction {
    CouponUsage.new {
        this.coupon = coupon
        this.user = this@useCoupon
    }

    coupon.items.forEach {
        player?.inventory?.addItem(it.item)
    }

    player?.sendMessage("쿠폰의 아이템이 지급되었습니다.")
}

fun User.canUseCoupon(coupon: Coupon): Boolean = transaction {
    return@transaction CouponUsage.find { CouponUsages.user eq this@canUseCoupon.id and (CouponUsages.coupon eq coupon.id) }.empty()
}