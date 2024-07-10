package io.github.hxxniverse.hobeaktown.feature.user

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoney
import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoneys
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.UserFatigue
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.UserFatigues
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Role
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Roles
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.UserKeyCard
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.UserKeyCards
import io.github.hxxniverse.hobeaktown.feature.stock.entity.UserStock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.UserStocks
import org.bukkit.Bukkit
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Users : UUIDTable() {
    val name = varchar("name", 255)
    val money = reference("money", UserMoneys)
//    val fatigue = reference("fatigue", UserFatigues)
//    val role = reference("role", UserKeyCards)
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var name by Users.name
    var money by UserMoney referencedOn Users.money
//    var fatigue by UserFatigue referencedOn  UserFatigues.curFatigue
//    var role by UserKeyCard referencedOn UserKeyCards.role

    val stock by UserStock referrersOn UserStocks.user

    val player get() = Bukkit.getPlayer(id.value)
}
