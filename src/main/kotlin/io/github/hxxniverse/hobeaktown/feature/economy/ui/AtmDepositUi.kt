package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.isPaperMoney
import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.feature.economy.util.toPaperMoney
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.inventory.icon
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat

class AtmDepositUi : CustomInventory("Atm Deposit", 54) {
    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

            button(ItemStack(Material.AIR), from = 2 to 2, to = 8 to 2) {
                val itemStack = it.currentItem ?: return@button
                player.inventory.addItem(itemStack)
                it.currentItem = null
            }

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
                val paperMoneys = it.inventory.contents.filter { it?.isPaperMoney() == true }.filterNotNull()
                val money = paperMoneys.sumOf { (it.toPaperMoney()?.money ?: 0) * it.amount }
                paperMoneys.forEach { it.amount = 0 }
                player.money += money
                player.sendMessage("입금 완료: ${DecimalFormat("#,###").format(money)}원")
            }
        }
        onPlayerInventoryClick {
            it.isCancelled = it.currentItem?.isPaperMoney() == false
        }
    }
}