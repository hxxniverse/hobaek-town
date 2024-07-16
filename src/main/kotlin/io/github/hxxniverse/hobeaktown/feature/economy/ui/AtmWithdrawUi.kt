package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.feature.economy.util.toPaperMoney
import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.hasSpace
import io.github.hxxniverse.hobeaktown.util.extension.sendErrorMessage
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat

class AtmWithdrawUi {

    private val moneys = listOf(
        500, 1_000, 10_000, 100_000, 1_000_000, 10_000_000, 100_000_000
    )

    fun open(player: Player) {
        AnvilInventory(
            title = "출금",
            text = "출금 금액을 입력해주세요",
            onClose = {
            },
            onClickResult = { result ->
                val money = result.text.replace("_", "").toIntOrNull()
                if (money == null) {
                    player.sendErrorMessage("숫자만 입력해주세요.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                if (money <= 0) {
                    player.sendErrorMessage("0보다 큰 금액을 입력해주세요.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                if (player.user.money < money) {
                    player.sendErrorMessage("돈이 부족합니다.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                var withdrawMoney: Int = money
                val paperMoney = mutableListOf<ItemStack>()

                moneys.reversed().forEach { m ->
                    val count: Int = (withdrawMoney / m)
                    if (count > 0) {
                        paperMoney.add(m.toPaperMoney().edit { setAmount(count) })
                        withdrawMoney -= m * count
                    }
                }

                if (!player.inventory.hasSpace(*paperMoney.toTypedArray())) {
                    player.sendErrorMessage("인벤토리 공간이 부족합니다.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                player.user.money -= money
                player.user.money += withdrawMoney
                player.inventory.addItem(*paperMoney.toTypedArray())
                player.sendInfoMessage("${DecimalFormat("#,###").format(money)}원을 출금하였습니다.")
                if (withdrawMoney > 0) {
                    player.sendInfoMessage("${DecimalFormat("#,###").format(withdrawMoney)}원은 다시 입금되었습니다.")
                }

                return@AnvilInventory listOf(AnvilGUI.ResponseAction.close())
            }
        ).open(player)
    }
}