package io.github.hxxniverse.hobeaktown.feature.stock.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object Stocks : IntIdTable() {
    val name = varchar("name", 50)
    val amount = integer("amount")
    val currentPrice = integer("current_price")
    val beforePrice = integer("before_price")
    val fluctuation = integer("fluctuation")
}

class Stock(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Stock>(Stocks) {
        fun new(name: String, amount: Int, currentPrice: Int, fluctuation: Int) = transaction {
            Stock.new {
                this.name = name
                this.amount = amount
                this.currentPrice = currentPrice
                this.beforePrice = currentPrice
                this.fluctuation = fluctuation
            }
        }
    }

    var name by Stocks.name
    var amount by Stocks.amount
    var currentPrice by Stocks.currentPrice
    var beforePrice by Stocks.beforePrice
    var fluctuation by Stocks.fluctuation
}