package io.github.hxxniverse.hobeaktown.feature.stock.util

import io.github.hxxniverse.hobeaktown.feature.stock.entity.*
import io.github.hxxniverse.hobeaktown.feature.user.user
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.and

fun Player.getStocks() = UserStock.find { UserStocks.user eq user.id }.toList()

fun Player.getStock(stock: Stock) =
    UserStock.find { (UserStocks.user eq user.id) and (UserStocks.stockId eq stock.id) }.firstOrNull()

fun Player.addStock(stock: Stock, amount: Int) {
    val playerStock = getStock(stock)
    if (playerStock == null) {
        UserStock.new(
            user = user,
            stock = stock,
            amount = amount,
        )
    } else {
        playerStock.amount += amount
    }
    StockTradeHistory.new(
        stock = stock,
        price = stock.currentPrice,
        type = TradeType.BUY,
        amount = amount,
    )
}

fun Player.removeStock(stock: Stock, amount: Int) {
    val playerStock = getStock(stock)
    if (playerStock != null) {
        playerStock.amount -= amount
        if (playerStock.amount <= 0) {
            playerStock.delete()
        }
    }
    StockTradeHistory.new(
        stock = stock,
        price = stock.currentPrice,
        type = TradeType.SELL,
        amount = -amount,
    )
}

fun Player.hasStock(stock: Stock, amount: Int) = (getStock(stock)?.amount ?: 0) >= amount
