package io.github.hxxniverse.hobeaktown.feature.stock.ui

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.util.toGraphItemStack
import io.github.hxxniverse.hobeaktown.feature.stock.util.toItemStack
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
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
                stock.toItemStack(player),
                from = 2 to 3,
                to = 3 to 4,
            ) {
            }

            button(
                stock.toGraphItemStack(),
                from = 5 to 3,
                to = 6 to 4,
            ) {
            }

            button(
                ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE)
                    .setDisplayName("구매")
                    .build(),
                from = 8 to 2,
                to = 9 to 3,
            ) {
                StockTradeAmountUi(TradeType.BUY, stock).open(player)
            }

            button(
                ItemStackBuilder(Material.RED_STAINED_GLASS_PANE)
                    .setDisplayName("판매")
                    .build(),
                from = 8 to 4,
                to = 9 to 5,
            ) {
                StockTradeAmountUi(TradeType.SELL, stock).open(player)
            }
        }
    }
}
