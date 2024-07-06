package io.github.hxxniverse.hobeaktown.feature.stock.ui

import io.github.hxxniverse.hobeaktown.feature.stock.PriceChangeTask
import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.util.toItemStack
import io.github.hxxniverse.hobeaktown.util.EntityListPager
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class StockStatusUi : CustomInventory("주식 상태", 54) {

    private val stock = EntityListPager(Stock, 4)

    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))
            // 주식 상태 UI 구성
            val buttonAreas = listOf(
                Pair(3, 1) to Pair(4, 2),
                Pair(6, 1) to Pair(7, 2),
                Pair(3, 4) to Pair(4, 5),
                Pair(6, 4) to Pair(7, 5),
            )
            stock.getCurrentPage().forEachIndexed { index, stock ->
                buttonAreas[index].let { (start, end) ->
                    button(
                        itemStack = stock.toItemStack(player),
                        from = start,
                        to = end,
                    ) {
                        // 구매 페이지 이동
                        StockTradeConfirmUi(stock).open(player)
                    }
                }
            }
            val interval = PriceChangeTask.getLeftInterval() / 1000
            button(
                3 to 5,
                itemStack = ItemStackBuilder(Material.CLOCK)
                    .setDisplayName("다음 주가 업데이트 시간")
                    .addLore("다음 주가 업데이트까지 남은 시간: ${interval / 60}분 ${interval % 60}초")
                    .addLore("클릭 시 새로고침")
                    .build(),
            ) {
                updateInventory()
            }
            if (stock.hasPreviousPage()) {
                button(
                    6 to 1,
                    itemStack = PREVIOUS_PAGE,
                ) {
                    stock.previousPage()
                    updateInventory()
                }
            }
            if (stock.hasNextPage()) {
                button(
                    6 to 9,
                    itemStack = NEXT_PAGE,
                ) {
                    stock.nextPage()
                    updateInventory()
                }
            }
        }
    }
}
