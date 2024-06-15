package io.github.hxxniverse.hobeaktown.feature.stock.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.TradeType
import io.github.hxxniverse.hobeaktown.feature.stock.util.addStock
import io.github.hxxniverse.hobeaktown.feature.stock.util.getStock
import io.github.hxxniverse.hobeaktown.feature.stock.util.removeStock
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.hxxniverse.hobeaktown.util.extension.text
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction


class StockTradeAmountUi(
    private val tradeType: TradeType,
    val stock: Stock,
) {
    fun open(player: Player) {
        AnvilInventory(
            title = "${if (tradeType == TradeType.BUY) "구매" else "판매"} 하실 수량을 입력해주세요.",
            text = "_",
            itemInputLeft = ItemStack(Material.PAPER).edit {
                addLore("거래 수량을 입력해주세요.")
            },
            itemOutput = ItemStack(Material.PAPER).edit {
                addLore("클릭 시 거래가 완료됩니다.")
            },
            onClickResult = {
                transaction {
                    val amount = it.text.replace("_", "").toIntOrNull() ?: return@transaction listOf(AnvilGUI.ResponseAction.close())
                    println("amount: $amount")
                    try {
                        tradeStock(player, stock, amount)
                        if (tradeType == TradeType.BUY) {
                            text(stock.name).append(text("을(를) $amount 주 구매하였습니다.")).send(player)
                        } else {
                            text(stock.name).append(text("을(를) $amount 주 판매하였습니다.")).send(player)
                        }
                        return@transaction listOf(AnvilGUI.ResponseAction.close())
                    } catch (e: IllegalArgumentException) {
                        text(e.message ?: "알 수 없는 오류가 발생하였습니다.").send(player)
                        return@transaction listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                    }
                }
            },
        ).open(player = player)
    }

    private fun tradeStock(player: Player, stock: Stock, amount: Int) = transaction {
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
                if (player.money < price) {
                    throw IllegalArgumentException("소지금이 부족합니다.")
                }

                player.money -= price
                stock.remainingAmount -= amount
                player.addStock(stock, amount)
            }

            TradeType.SELL -> {
                // 판매하려는 주식이 소지 주식보다 많을 경우
                if ((player.getStock(stock)?.amount ?: 0) < amount) {
                    throw IllegalArgumentException("판매가능한 주식이 부족합니다.")
                }

                player.money += price
                stock.remainingAmount += amount
                player.removeStock(stock, amount)
            }
        }
    }
}
