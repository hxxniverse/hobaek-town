package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.cash
import io.github.hxxniverse.hobeaktown.feature.economy.util.toCashCoin
import io.github.hxxniverse.hobeaktown.feature.economy.util.toPaperMoney
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.hasSpace
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat

class AtmCashWithdrawUi {

    private val moneys = listOf(
        500, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000
    )

    fun open(player: Player) {
        AnvilInventory(
            title = "코인 출금",
            text = "금액을 입력해주세요",
            itemInputLeft = ItemStack(Material.PAPER).edit {
                addLore("금액을 입력해주세요.")
            },
            itemOutput = ItemStack(Material.PAPER).edit {
                addLore("클릭 시 코인 출금이 완료됩니다.")
            },
            onClose = {
            },
            onClickResult = { result ->
                val cash = result.text.replace("_", "").toIntOrNull()
                if (cash == null) {
                    player.sendMessage("숫자만 입력해주세요.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                if (cash <= 0) {
                    player.sendMessage("0보다 큰 금액을 입력해주세요.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                if (player.cash < cash) {
                    player.sendMessage("코인가 부족합니다.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                var withdrawMoney: Int = cash
                val paperMoney = mutableListOf<ItemStack>()

                moneys.reversed().forEach { m ->
                    val count: Int = (withdrawMoney / m)
                    if (count > 0) {
                        paperMoney.add(m.toCashCoin().edit { setAmount(count) })
                        withdrawMoney -= m * count
                    }
                }

                if (!player.inventory.hasSpace(*paperMoney.toTypedArray())) {
                    player.sendMessage("인벤토리 공간이 부족합니다.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                player.cash -= cash
                player.cash += withdrawMoney
                player.inventory.addItem(*paperMoney.toTypedArray())
                player.sendMessage("${DecimalFormat("#,###").format(cash)} 코인을 출금하였습니다.")
                if (withdrawMoney > 0) {
                    player.sendMessage("${DecimalFormat("#,###").format(withdrawMoney)} 코인는 다시 입금되었습니다.")
                }

                return@AnvilInventory listOf(AnvilGUI.ResponseAction.close())
            }
        ).open(player)
    }
}