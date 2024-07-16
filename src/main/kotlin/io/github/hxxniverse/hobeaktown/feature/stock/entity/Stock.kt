package io.github.hxxniverse.hobeaktown.feature.stock.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

object Stocks : IntIdTable() {
    val name = varchar("name", 50)
    val remainingAmount = integer("remaining_amount")
    val currentPrice = integer("current_price")
    val beforePrice = integer("before_price")
    val fluctuation = integer("fluctuation")
}

class Stock(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Stock>(Stocks) {
        fun new(
            name: String,
            remainingAmount: Int,
            currentPrice: Int,
            fluctuation: Int,
        ) = loggedTransaction {
            Stock.new {
                this.name = name
                this.remainingAmount = remainingAmount
                this.currentPrice = currentPrice
                this.beforePrice = currentPrice
                this.fluctuation = fluctuation
            }
        }
    }

    var name by Stocks.name
    var remainingAmount by Stocks.remainingAmount
    var currentPrice by Stocks.currentPrice
    var beforePrice by Stocks.beforePrice
    var fluctuation by Stocks.fluctuation
}
