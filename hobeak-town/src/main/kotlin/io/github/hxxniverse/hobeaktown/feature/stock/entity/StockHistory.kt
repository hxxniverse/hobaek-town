package io.github.hxxniverse.hobeaktown.feature.stock.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDate
import java.time.LocalDateTime

object StockHistories : IntIdTable() {
    val stockId = reference("stock_id", Stocks)
    val price = integer("price")
    val fluctuation = integer("fluctuation")
    val createdAt = datetime("created_at")
}

class StockHistory(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StockHistory>(StockHistories) {
        fun new(
            stock: Stock,
            price: Int,
            fluctuation: Int,
            createdAt: LocalDateTime = LocalDateTime.now(),
        ) = StockHistory.new {
            this.stock = stock
            this.price = price
            this.fluctuation = fluctuation
            this.createdAt = createdAt
        }
    }

    var stock by Stock referencedOn StockHistories.stockId
    var price by StockHistories.price
    var fluctuation by StockHistories.fluctuation
    var createdAt by StockHistories.createdAt
}
