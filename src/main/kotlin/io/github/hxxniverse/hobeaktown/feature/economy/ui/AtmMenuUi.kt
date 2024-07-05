package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class AtmMenuUi : CustomInventory("Atm Menu", 54) {
    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

            button(
                itemStack = icon { type = Material.BLUE_STAINED_GLASS_PANE; name = component("입금") },
                from = 2 to 1,
                to = 2 to 3
            ) {
                AtmDepositUi().open(player)
            }

            button(
                itemStack = icon { type = Material.PURPLE_STAINED_GLASS_PANE; name = component("송금") },
                from = 2 to 7,
                to = 2 to 9
            ) {
                AtmRemittanceRecipientUi().open(player)
            }

            button(
                itemStack = icon { type = Material.RED_STAINED_GLASS_PANE; name = component("출금") },
                from = 5 to 1,
                to = 5 to 3
            ) {
                AtmWithdrawUi().open(player)
            }

            button(
                itemStack = icon { type = Material.YELLOW_STAINED_GLASS_PANE; name = component("코인 출금") },
                from = 5 to 7,
                to = 5 to 9,
            ) {
                AtmCashWithdrawUi().open(player)
            }
        }
    }
}

