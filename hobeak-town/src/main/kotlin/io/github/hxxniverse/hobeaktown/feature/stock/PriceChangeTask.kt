package io.github.hxxniverse.hobeaktown.feature.stock

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.StockHistory
import io.github.hxxniverse.hobeaktown.util.BaseScheduler
import io.github.hxxniverse.hobeaktown.util.extension.text
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.transactions.transaction

object PriceChangeTask : BaseScheduler(
    repeat = true,
    interval = StockConfig.configData.fluctuationTime.toLong() * 1000
) {
    override suspend fun onStart() {
    }

    override suspend fun onEach(count: Int) {
        transaction {
            Stock.all().forEach {
                if (it.currentPrice == -1) {
                    return@forEach
                }
                // 가격 변동
                it.beforePrice = it.currentPrice
                val fluctuation = (-it.fluctuation..it.fluctuation).random()
                val diff = it.currentPrice * fluctuation / 100
                it.currentPrice = (it.currentPrice * (1 + fluctuation / 100.0)).toInt()
                // 만약 변동된 가격이 0 원 아래로 가면 상장페지로 -1로 지정
                if (it.currentPrice <= 0) {
                    it.currentPrice = -1
                    it.beforePrice = -1
                    Bukkit.broadcast("${it.name}이 상장폐지 되었습니다.".text())
                }
                transaction {
                    StockHistory.new(
                        stock = it,
                        price = it.currentPrice,
                        fluctuation = diff,
                    )
                }
            }
        }
        Bukkit.broadcast("주식 시장 가격이 변동되었습니다.".text())
    }

    override fun onStop() {
    }
}
