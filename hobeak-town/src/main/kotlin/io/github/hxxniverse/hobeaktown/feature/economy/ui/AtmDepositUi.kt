package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.toPaperMoney
import io.github.hxxniverse.hobeaktown.util.extension.hasSpace
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.inventory.icon
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class AtmDepositUi : CustomInventory("Atm Deposit", 54) {

    private var selectMoney = 0
    private var selectAmount = 0

    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

            val moneys = listOf(
                500, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000
            )
            moneys.forEachIndexed { index, money ->
                button(
                    itemStack = money.toPaperMoney(),
                    index = 2 + index to 2,
                ) {
                    selectMoney = money
                    selectAmount = if (it.isRightClick) {
                        max(0, selectAmount - 1)
                    } else {
                        min(64, selectAmount + 1)
                    }
                    updateInventory()
                }
            }

            button(
                itemStack = icon { type = Material.PAPER; name = text("수량: $selectAmount") },
                index = 5 to 4
            )

            button(
                itemStack = icon { type = Material.RED_STAINED_GLASS_PANE; name = text("취소") },
                from = 2 to 4,
                to = 3 to 5
            ) {
                AtmMenuUi().open(player)
            }
            button(
                itemStack = icon { type = Material.GREEN_STAINED_GLASS_PANE; name = text("확인") },
                from = 7 to 4,
                to = 8 to 5
            ) {
                val itemStacks = selectMoney.toPaperMoney().apply { amount = selectAmount }
                if (!player.inventory.hasSpace(itemStacks)) {
                    player.sendMessage("인벤토리에 공간이 부족합니다.")
                    return@button
                }
                player.inventory.addItem(itemStacks)
            }
        }
    }
}