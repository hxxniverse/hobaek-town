package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.*
import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat

class AtmDepositUi : CustomInventory("Atm Deposit", 54) {
    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

            button(from = 2 to 2, to = 2 to 8, ItemStack(Material.AIR)) {
                val itemStack = it.currentItem ?: return@button
                player.inventory.addItem(itemStack)
                it.currentItem = null
            }

            button(
                itemStack = icon { type = Material.RED_STAINED_GLASS_PANE; name = component("취소") },
                from = 4 to 2,
                to = 5 to 3
            ) {
                AtmMenuUi().open(player)
            }
            button(
                itemStack = icon { type = Material.GREEN_STAINED_GLASS_PANE; name = component("확인") },
                from = 4 to 7,
                to = 5 to 8
            ) {
                val paperMoneys = it.inventory.contents.filter { item -> item?.isPaperMoney() == true }.filterNotNull()
                val cashCoins = it.inventory.contents.filter { item -> item?.isCashCoin() == true }.filterNotNull()

                val money = paperMoneys.sumOf { item -> (item.toPaperMoney()?.money ?: 0) * item.amount }
                val cashCoin = cashCoins.sumOf { item -> (item.toCashCoin()?.money ?: 0) * item.amount }

                paperMoneys.forEach { item -> item.amount = 0 }
                cashCoins.forEach { item -> item.amount = 0 }

                player.user.money += money
                player.user.cash += cashCoin

                if (money > 0) {
                    player.sendInfoMessage("입금 완료: ${DecimalFormat("#,###").format(money)}원")
                }
                if (cashCoin > 0) {
                    player.sendInfoMessage("입금 완료: ${DecimalFormat("#,###").format(cashCoin)}코인")
                }
            }
        }
        onPlayerInventoryClick {
            it.isCancelled = it.currentItem?.isPaperMoney() == false && it.currentItem?.isCashCoin() == false
        }
    }
}