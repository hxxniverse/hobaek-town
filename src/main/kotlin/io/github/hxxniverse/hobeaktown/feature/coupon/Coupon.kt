package io.github.hxxniverse.hobeaktown.feature.coupon

import io.github.hxxniverse.hobeaktown.feature.user.User
import io.github.hxxniverse.hobeaktown.feature.user.User.Companion.referrersOn
import io.github.hxxniverse.hobeaktown.feature.user.Users
import io.github.hxxniverse.hobeaktown.util.database.itemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Coupons : IntIdTable() {
    val name = varchar("name", 255)
    val expiredDate = datetime("expired_date")
}

class Coupon(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Coupon>(Coupons)

    var name by Coupons.name
    var expiredDate by Coupons.expiredDate

    val items by CouponItem referrersOn CouponItems.coupon
}

object CouponItems : IntIdTable() {
    val coupon = reference("coupon", Coupons)
    val item = itemStack("item")
}

class CouponItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CouponItem>(CouponItems)

    var coupon by Coupon referencedOn CouponItems.coupon
    var item by CouponItems.item
}


object CouponUsages : IntIdTable() {
    val user = reference("user", Users)
    val coupon = reference("coupon", Coupons)
}

class CouponUsage(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CouponUsage>(CouponUsages)

    var user by User referencedOn CouponUsages.user
    var coupon by Coupon referencedOn CouponUsages.coupon
}

