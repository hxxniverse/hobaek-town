package io.github.hxxniverse.hobeaktown.feature.stock

import io.github.hxxniverse.hobeaktown.feature.stock.entity.*
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class StockFeature : BaseFeature {
    override fun enable(plugin: JavaPlugin) {
        transaction {
            SchemaUtils.create(Stocks, UserStocks, StockHistories, StockTradeHistories)
        }
        StockConfig.load()
        StockCommand().register(plugin)
        PriceChangeTask.start()
    }

    override fun disable(plugin: JavaPlugin) {
    }
}
