package io.github.hxxniverse.hobeaktown.feature.stock.util

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.StockHistories
import io.github.hxxniverse.hobeaktown.feature.stock.entity.StockHistory
import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.extension.component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import java.text.DecimalFormat
import kotlin.math.abs

fun Stock.toItemStack(player: Player): ItemStack = loggedTransaction {
    // 현재 주식가격 ( [화살표] 변동폭 )
    val currentPriceStr = DecimalFormat("#,###").format(currentPrice)
    val isUp = beforePrice - currentPrice < 0
    val beforePriceStr = DecimalFormat("#,###").format(abs(beforePrice - currentPrice))
    // 상승 또는 하락 표시
    val arrow = if (isUp) "▲" else "▼"
    return@loggedTransaction ItemStackBuilder(material = Material.PAPER)
        .setDisplayName(name.component())
        .addLore("")
        .addLore(
            component("가격: ").append(currentPriceStr.component(NamedTextColor.GOLD))
                .append(" $arrow $beforePriceStr".component(if (isUp) NamedTextColor.RED else NamedTextColor.BLUE))
        )
        .addLore(component("남은갯수: ").append(remainingAmount.toString().component()))
        .addLore(component("보유갯수: ").append((player.user.getStock(this@toItemStack)?.amount ?: 0).toString().component()))
        .build()
}

fun Stock.toGraphItemStack(): ItemStack = loggedTransaction {
    val histories = StockHistory.find { StockHistories.stockId eq this@toGraphItemStack.id }.toList()
    if (histories.isEmpty()) return@loggedTransaction ItemStackBuilder(material = Material.RED_STAINED_GLASS_PANE)
        .setDisplayName("최근 5회 변동추이")
        .addLore("데이터가 없습니다.")
        .build()
    val historyChunk = histories.chunked(5).first().reversed()
    val isCurrentUp = beforePrice - currentPrice < 0
    return@loggedTransaction ItemStackBuilder(material = if (isCurrentUp) Material.RED_STAINED_GLASS_PANE else Material.BLUE_STAINED_GLASS_PANE)
        .setDisplayName("최근 5회 변동추이")
        .addLore("")
        .apply {
            historyChunk.forEachIndexed { index, stockHistory ->
                val isHistoryUp = stockHistory.fluctuation > 0
                val color = if (isHistoryUp) NamedTextColor.RED else NamedTextColor.BLUE
                val decimalFormat = DecimalFormat("#,###")

                (if (index == 0) "현재 " else "$index 시간 전 ").component(NamedTextColor.GRAY)
                    .append(decimalFormat.format(stockHistory.price).component(NamedTextColor.GOLD))
                    .append(" ${if (isHistoryUp) "▲" else "▼"} ".component(color))
                    .append(decimalFormat.format(stockHistory.fluctuation).component(color))
                    .let { lore -> addLore(lore) }
            }
        }
        .build()
}
