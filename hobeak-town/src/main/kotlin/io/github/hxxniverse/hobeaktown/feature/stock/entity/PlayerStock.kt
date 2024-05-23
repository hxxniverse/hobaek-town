package io.github.hxxniverse.hobeaktown.feature.stock.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object PlayerStocks : UUIDTable() {
    val stockId = reference("stock_id", Stocks)
    val amount = integer("amount")
}

class PlayerStock(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerStock>(PlayerStocks) {
        fun new(
            playerUUID: UUID,
            stock: Stock,
            amount: Int,
        ) = PlayerStock.new(playerUUID) {
            this.stock = stock
            this.amount = amount
        }
    }

    var stock by Stock referencedOn PlayerStocks.stockId
    var amount by PlayerStocks.amount
}
