package io.github.hxxniverse.hobeaktown.feature.stock.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

enum class TradeType {
    BUY,
    SELL,
}

object StockTradeHistories : IntIdTable() {
    val stock = reference("stock_id", Stocks)
    val price = integer("price")
    val amount = integer("amount")
    val type = enumeration("type", TradeType::class)
    val createdAt = datetime("created_at")
}

class StockTradeHistory(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<StockTradeHistory>(StockTradeHistories) {
        fun new(
            stock: Stock,
            price: Int,
            amount: Int,
            type: TradeType,
        ) = StockTradeHistory.new {
            this.stock = stock
            this.price = price
            this.amount = amount
            this.type = type
            this.createdAt = LocalDateTime.now()
        }
    }

    var stock by Stock referencedOn StockTradeHistories.stock
    var price by StockTradeHistories.price
    var amount by StockTradeHistories.amount
    var type by StockTradeHistories.type
    var createdAt by StockTradeHistories.createdAt
}