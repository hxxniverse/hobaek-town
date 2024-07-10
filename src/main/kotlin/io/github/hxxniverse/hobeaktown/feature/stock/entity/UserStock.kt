package io.github.hxxniverse.hobeaktown.feature.stock.entity

import io.github.hxxniverse.hobeaktown.feature.user.User
import io.github.hxxniverse.hobeaktown.feature.user.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object UserStocks : IntIdTable() {
    val user = reference("user", Users)
    val stockId = reference("stock_id", Stocks)
    val amount = integer("amount")
}

class UserStock(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserStock>(UserStocks) {
        fun new(
            user: User,
            stock: Stock,
            amount: Int,
        ) = UserStock.new {
            this.user = user.id
            this.stock = stock
            this.amount = amount
        }
    }

    var user by UserStocks.user
    var stock by Stock referencedOn UserStocks.stockId
    var amount by UserStocks.amount
}
