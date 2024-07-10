package io.github.hxxniverse.hobeaktown.feature.user

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoney
import io.github.hxxniverse.hobeaktown.feature.fatigue.Status
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.UserFatigue
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Role
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Roles
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.UserKeyCard
import io.github.hxxniverse.hobeaktown.feature.stock.entity.UserStock
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

val Player.user: User
    get() = transaction {
        User.findById(uniqueId) ?: User.new(uniqueId) {
            name = this@user.name
            money = UserMoney.new {
                money = 0
                cash = 0
            }
//            role = UserKeyCard.findOrCreate(this@user.uniqueId)
//            fatigue = UserFatigue.new {
//                curFatigue = 100
//                maxFatigue = 100
//                status = Status.Normal.toString()
//            }
        }
    }