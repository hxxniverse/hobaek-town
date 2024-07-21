package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.extension.sendErrorMessage
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.entity.Player

class AtmRemittanceAmountUi(
    private val target: Player,
) {
    fun open(player: Player) {
        AnvilInventory(
            title = "송금 금액 입력",
            text = "송금하실 금액을 입력해주세요",
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

                player.user.money -= money
                target.user.money += money

                player.sendInfoMessage("송금이 완료되었습니다.")

                if (target.isOnline) {
                    target.sendInfoMessage("${player.name}님으로부터 ${money}원을 받았습니다.")
                }

                return@AnvilInventory listOf(AnvilGUI.ResponseAction.close())
            }
        ).open(player)
    }
}
