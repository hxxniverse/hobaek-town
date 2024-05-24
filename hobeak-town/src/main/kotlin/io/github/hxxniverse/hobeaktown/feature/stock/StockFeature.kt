package io.github.hxxniverse.hobeaktown.feature.stock

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.StockHistories
import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stocks
import io.github.hxxniverse.hobeaktown.feature.stock.entity.UserStocks
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class StockFeature : BaseFeature {
    override fun enable(plugin: JavaPlugin) {
        transaction {
            SchemaUtils.drop(Stocks, UserStocks, StockHistories)
            SchemaUtils.create(Stocks, UserStocks, StockHistories)

            Stock.new("후스텔", 100, 5000000, 70)
            Stock.new("후뱅크", 100, 5000000, 70)
            Stock.new("후페이", 100, 5000000, 70)
            Stock.new("후스토", 100, 5000000, 70)
            Stock.new("후삼성", 100, 5000000, 70)
        }
        StockConfig.load()
        StockCommand().register(plugin)
        PriceChangeTask.start()
    }

    override fun disable(plugin: JavaPlugin) {
    }
}
