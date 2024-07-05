package io.github.hxxniverse.hobeaktown.feature.user

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import io.github.hxxniverse.hobeaktown.feature.coupon.Coupon
import io.github.hxxniverse.hobeaktown.feature.coupon.CouponUsage
import io.github.hxxniverse.hobeaktown.feature.coupon.CouponUsages
import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoney
import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoneys
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.UserKeyCard
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.UserKeyCards
import io.github.hxxniverse.hobeaktown.feature.stock.entity.UserStock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.UserStocks
import org.bukkit.Bukkit
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Users : UUIDTable() {
    val name = varchar("name", 255)
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var name by Users.name

    val money by UserMoney referencedOn UserMoneys.id
    val stock by UserStock referrersOn UserStocks.id

    val player get() = Bukkit.getPlayer(id.value)
}