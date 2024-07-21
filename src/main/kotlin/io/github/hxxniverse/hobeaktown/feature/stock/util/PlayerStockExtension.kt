package io.github.hxxniverse.hobeaktown.feature.stock.util

import io.github.hxxniverse.hobeaktown.feature.stock.entity.*
import io.github.hxxniverse.hobeaktown.feature.user.User
import org.jetbrains.exposed.sql.and

fun User.getStocks() = UserStock.find { UserStocks.user eq id }.toList()

fun User.getStock(stock: Stock) =
    UserStock.find { (UserStocks.user eq id) and (UserStocks.stockId eq stock.id) }.firstOrNull()

fun User.addStock(stock: Stock, amount: Int) {
    val playerStock = getStock(stock)
    if (playerStock == null) {
        UserStock.new(
            user = this@addStock,
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

fun User.removeStock(stock: Stock, amount: Int) {
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

fun User.hasStock(stock: Stock, amount: Int) = (getStock(stock)?.amount ?: 0) >= amount
