package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.edit
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AtmRemittanceRecipientUi {
    fun open(player: Player) {

        AnvilInventory(
            title = "송금자 입력",
            text = "보낼 사람을 입력해주세요",
            itemInputLeft = ItemStack(Material.PAPER).edit {
                addLore("보낼 사람을 입력해주세요")
            },
            itemOutput = ItemStack(Material.PAPER).edit {
                addLore("클릭 시 금액 입력으로 넘어갑니다.")
            },
            onClose = {
            },
            onClickResult = { result ->
                val target = player.server.getPlayer(result.text)

                if (target == null) {
                    player.sendMessage("존재하지 않는 플레이어입니다.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                AtmRemittanceAmountUi(target).open(player)
                return@AnvilInventory listOf(AnvilGUI.ResponseAction.close())
            }
        ).open(player)
    }
}
