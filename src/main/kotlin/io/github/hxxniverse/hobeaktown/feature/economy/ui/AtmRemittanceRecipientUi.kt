package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.extension.sendErrorMessage
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.entity.Player

class AtmRemittanceRecipientUi {
    fun open(player: Player) {

        AnvilInventory(
            title = "송금자 입력",
            text = "보낼 사람을 입력해주세요",
            onClose = {
            },
            onClickResult = { result ->
                val target = player.server.getPlayer(result.text)

                if (target == null) {
                    player.sendErrorMessage("존재하지 않는 플레이어입니다.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                AtmRemittanceAmountUi(target).open(player)
                return@AnvilInventory listOf(AnvilGUI.ResponseAction.close())
            }
        ).open(player)
    }
}
