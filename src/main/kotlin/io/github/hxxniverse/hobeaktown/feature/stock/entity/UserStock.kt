package io.github.hxxniverse.hobeaktown.feature.stock.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import java.util.*

object UserStocks : IntIdTable() {
    val uuid = uuid("uuid")
    val stockId = reference("stock_id", Stocks)
    val amount = integer("amount")
}

class UserStock(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserStock>(UserStocks) {
        fun new(
            uuid: UUID,
            stock: Stock,
            amount: Int,
        ) = UserStock.new {
            this.uuid = uuid
            this.stock = stock
            this.amount = amount
        }
    }

    var uuid by UserStocks.uuid
    var stock by Stock referencedOn UserStocks.stockId
    var amount by UserStocks.amount
}
