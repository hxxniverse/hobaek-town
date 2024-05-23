package io.github.hxxniverse.hobeaktown.feature.stock.util

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.StockHistories
import io.github.hxxniverse.hobeaktown.feature.stock.entity.StockHistory
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.extension.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import kotlin.math.abs

fun Stock.toItemStack(player: Player): ItemStack = transaction {
    // 현재 주식가격 ( [화살표] 변동폭 )
    val currentPriceStr = DecimalFormat("#,###").format(currentPrice)
    val isUp = beforePrice - currentPrice < 0
    val beforePriceStr = DecimalFormat("#,###").format(abs(beforePrice - currentPrice))
    // 상승 또는 하락 표시
    val arrow = if (isUp) "▲" else "▼"
    return@transaction ItemStackBuilder(material = Material.PAPER)
        .setDisplayName(name.text())
        .addLore("")
        .addLore(text("가격: ").append(currentPriceStr.text(NamedTextColor.GOLD)).append(" $arrow $beforePriceStr".text(if (isUp) NamedTextColor.RED else NamedTextColor.BLUE)))
        .addLore(text("남은갯수: ").append(remainingAmount.toString().text()))
        .addLore(text("보유갯수: ").append((player.getStock(this@toItemStack)?.amount ?: 0).toString().text()))
        .build()
}

fun Stock.toGraphItemStack(): ItemStack = transaction {
    val histories = StockHistory.find { StockHistories.stockId eq this@toGraphItemStack.id }.toList().chunked(5).first().reversed()
    val isCurrentUp = beforePrice - currentPrice < 0
    return@transaction ItemStackBuilder(material = if (isCurrentUp) Material.RED_STAINED_GLASS_PANE else Material.BLUE_STAINED_GLASS_PANE)
        .setDisplayName("최근 5회 변동추이")
        .addLore("")
        .apply {
            histories.forEachIndexed { index, stockHistory ->
                val isHistoryUp = stockHistory.fluctuation > 0
                val color = if (isHistoryUp) NamedTextColor.RED else NamedTextColor.BLUE
                val decimalFormat = DecimalFormat("#,###")

                (if (index == 0) "현재 " else "$index 시간 전 ").text(NamedTextColor.GRAY)
                    .append(decimalFormat.format(stockHistory.price).text(NamedTextColor.GOLD))
                    .append(" ${if (isHistoryUp) "▲" else "▼"} ".text(color))
                    .append(decimalFormat.format(stockHistory.fluctuation).text(color))
                    .let { lore -> addLore(lore) }
            }
        }
        .build()
}
