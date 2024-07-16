package io.github.hxxniverse.hobeaktown.feature.stock

import io.github.hxxniverse.hobeaktown.feature.stock.entity.*
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class StockFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(Stocks, UserStocks, StockHistories, StockTradeHistories)
        }
        StockConfig.load()
        StockCommand().register(plugin)
        PriceChangeTask.start()
    }

    override fun onDisable(plugin: JavaPlugin) {
    }
}
