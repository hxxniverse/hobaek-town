package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.isPaperMoney
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.inventory.icon
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class AtmWithdrawUi : CustomInventory("Atm Withdraw", 54) {
    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

            // 2,2 ~ 8,2 empty
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
                // deposit
            }
        }
        onPlayerInventoryClick {
            val item = it.currentItem ?: return@onPlayerInventoryClick

            if (!item.isPaperMoney()) {
                it.isCancelled = true
            }

            // 위의 인벤토리로 이동하는 로직이 필요함..
        }
    }
}