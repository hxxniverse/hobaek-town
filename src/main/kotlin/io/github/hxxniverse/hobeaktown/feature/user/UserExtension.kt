package io.github.hxxniverse.hobeaktown.feature.user

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoney
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
        }
    }