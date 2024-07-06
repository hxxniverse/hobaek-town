package io.github.hxxniverse.hobeaktown.feature.stock.ui

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.TradeType
import io.github.hxxniverse.hobeaktown.feature.stock.util.toGraphItemStack
import io.github.hxxniverse.hobeaktown.feature.stock.util.toItemStack
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class StockTradeConfirmUi(
    val stock: Stock,
) : CustomInventory("Trade Confirm", 54) {
    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

            button(
                from = 3 to 2,
                to = 4 to 3,
                stock.toItemStack(player),
            ) {
            }

            button(
                from = 3 to 5,
                to = 4 to 6,
                stock.toGraphItemStack(),
            ) {
            }

            button(
                from = 2 to 8,
                to = 3 to 9,
                icon {
                    type = Material.GREEN_STAINED_GLASS_PANE
                    name = "구매".component()
                }
            ) {
                StockTradeAmountUi(TradeType.BUY, stock).open(player)
            }

            button(
                from = 4 to 8,
                to = 5 to 9,
                icon {
                    type = Material.RED_STAINED_GLASS_PANE
                    name = "판매".component()
                }
            ) {
                StockTradeAmountUi(TradeType.SELL, stock).open(player)
            }
        }
    }
}
