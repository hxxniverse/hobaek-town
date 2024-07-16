package io.github.hxxniverse.hobeaktown.feature.stock.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.TradeType
import io.github.hxxniverse.hobeaktown.feature.stock.util.addStock
import io.github.hxxniverse.hobeaktown.feature.stock.util.getStock
import io.github.hxxniverse.hobeaktown.feature.stock.util.removeStock
import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.hxxniverse.hobeaktown.util.extension.component
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.entity.Player
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction


class StockTradeAmountUi(
    private val tradeType: TradeType,
    val stock: Stock,
) {
    fun open(player: Player) {
        AnvilInventory(
            title = "${if (tradeType == TradeType.BUY) "구매" else "판매"} 하실 수량을 입력해주세요.",
            text = "_",
            onClickResult = {
                loggedTransaction {
                    val amount = it.text.replace("_", "").toIntOrNull() ?: return@loggedTransaction listOf(AnvilGUI.ResponseAction.close())
                    try {
                        tradeStock(player, stock, amount)
                        if (tradeType == TradeType.BUY) {
                            component(stock.name).append(component("을(를) $amount 주 구매하였습니다.")).send(player)
                        } else {
                            component(stock.name).append(component("을(를) $amount 주 판매하였습니다.")).send(player)
                        }
                        return@loggedTransaction listOf(AnvilGUI.ResponseAction.close())
                    } catch (e: IllegalArgumentException) {
                        component(e.message ?: "알 수 없는 오류가 발생하였습니다.").send(player)
                        return@loggedTransaction listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                    }
                }
            },
        ).open(player = player)
    }

    private fun tradeStock(player: Player, stock: Stock, amount: Int) = loggedTransaction {
        // 주식 정보 최신화로 가져옴
        val price = stock.currentPrice * amount

        // 구매 또는 판매 처리
        when (tradeType) {
            TradeType.BUY -> {
                // 구매하려는 주식이 남은 주식보다 많을 경우
                if (stock.remainingAmount < amount) {
                    throw IllegalArgumentException("구매가능한 주식이 부족합니다.")
                }

                // 구매하려는 주식이 가격보다 많을 경우
                if (player.user.money < price) {
                    throw IllegalArgumentException("소지금이 부족합니다.")
                }

                player.user.money -= price
                stock.remainingAmount -= amount
                player.user.addStock(stock, amount)
            }

            TradeType.SELL -> {
                // 판매하려는 주식이 소지 주식보다 많을 경우
                if ((player.user.getStock(stock)?.amount ?: 0) < amount) {
                    throw IllegalArgumentException("판매가능한 주식이 부족합니다.")
                }

                player.user.money += price
                stock.remainingAmount += amount
                player.user.removeStock(stock, amount)
            }
        }
    }
}
