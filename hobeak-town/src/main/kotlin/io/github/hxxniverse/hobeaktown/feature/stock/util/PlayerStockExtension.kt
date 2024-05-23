package io.github.hxxniverse.hobeaktown.feature.stock.util

import io.github.hxxniverse.hobeaktown.feature.stock.entity.PlayerStock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.PlayerStocks
import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.and

fun Player.getStocks() = PlayerStock.find { PlayerStocks.id eq uniqueId }.toList()

fun Player.getStock(stock: Stock) = PlayerStock.find { (PlayerStocks.id eq uniqueId) and (PlayerStocks.stockId eq stock.id) }.firstOrNull()

fun Player.addStock(stock: Stock, amount: Int) {
    val playerStock = getStock(stock)
    if (playerStock == null) {
        PlayerStock.new(uniqueId) {
            this.stock = stock
            this.amount = amount
        }
    } else {
        playerStock.amount += amount
    }
}

fun Player.removeStock(stock: Stock, amount: Int) {
    val playerStock = getStock(stock)
    if (playerStock != null) {
        playerStock.amount -= amount
        if (playerStock.amount <= 0) {
            playerStock.delete()
        }
    }
}

fun Player.hasStock(stock: Stock, amount: Int) = (getStock(stock)?.amount ?: 0) >= amount
