package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.inventory.icon
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class AtmMenuUi : CustomInventory("Atm Menu", 54) {
    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

            button(
                itemStack = icon { type = Material.BLUE_STAINED_GLASS_PANE; name = text("입금") },
                from = 1 to 2,
                to = 3 to 2
            ) {
                AtmDepositUi().open(player)
            }

            button(
                itemStack = icon { type = Material.PURPLE_STAINED_GLASS_PANE; name = text("송금") },
                from = 7 to 2,
                to = 9 to 2
            ) {
                AtmRemittanceRecipientUi().open(player)
            }

            button(
                itemStack = icon { type = Material.RED_STAINED_GLASS_PANE; name = text("출금") },
                from = 1 to 5,
                to = 3 to 5
            ) {
                AtmWithdrawUi().open(player)
            }

            button(
                itemStack = icon { type = Material.YELLOW_STAINED_GLASS_PANE; name = text("캐시 출금") },
                from = 7 to 5,
                to = 9 to 5,
            ) {
                AtmCashWithdrawUi().open(player)
            }
        }
    }
}

