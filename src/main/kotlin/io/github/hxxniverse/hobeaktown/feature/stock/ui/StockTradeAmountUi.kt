package io.github.hxxniverse.hobeaktown.feature.stock.ui

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.TradeType
import io.github.hxxniverse.hobeaktown.feature.stock.util.addStock
import io.github.hxxniverse.hobeaktown.feature.stock.util.removeStock
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction


class StockTradeAmountUi(
    private val tradeType: TradeType,
    val stock: Stock,
) : CustomInventory("${if (tradeType == TradeType.BUY) "구매" else "판매"}하실 수량을 입력해주세요", InventoryType.ANVIL) {
    init {
        inventory {
            setItem(
                0,
                ItemStack(Material.GREEN_STAINED_GLASS_PANE).apply {
                    itemMeta = itemMeta.apply {
                        displayName("".text())
                    }
                },
            )
            setItem(
                1,
                ItemStack(Material.GREEN_STAINED_GLASS_PANE).apply {
                    itemMeta = itemMeta.apply {
                        displayName("".text())
                    }
                },
            )
            setItem(
                2,
                ItemStack(Material.GREEN_STAINED_GLASS_PANE).apply {
                    itemMeta = itemMeta.apply {
                        displayName("확인".text())
                    }
                },
            ) {
                // trade
                val amount = 0

                transaction {
                    // 주식 정보 최신화로 가져옴
                    val stock = Stock.findById(stock.id) ?: return@transaction
                    val price = stock.currentPrice

                    // 구매 또는 판매 처리
                    when (tradeType) {
                        TradeType.BUY -> {
                            // 구매하려는 주식이 남은 주식보다 많을 경우
                            if (stock.remainingAmount < amount) {
                                player.sendMessage("잔여 주식이 부족합니다.")
                                return@transaction
                            }

                            // TODO 금액 차감

                            stock.remainingAmount -= amount
                            player.addStock(stock, amount)
                            player.sendMessage("구매 완료")
//                            StockStatusUi().open(player)
                        }

                        TradeType.SELL -> {
                            // TODO 금액 추가

                            stock.remainingAmount += amount
                            player.removeStock(stock, amount)
                            player.sendMessage("판매 완료")
//                            StockStatusUi().open(player)
                        }
                    }
                }
            }
        }
    }
}
