package io.github.hxxniverse.hobeaktown.feature.economy.ui

import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.edit
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AtmRemittanceAmountUi(
    private val target: Player,
) {
    fun open(player: Player) {
        AnvilInventory(
            title = "보낼 금액을 입력해주세요",
            text = "_",
            itemInputLeft = ItemStack(Material.PAPER).edit {
                addLore("보낼 금액을 입력해주세요.")
            },
            itemOutput = ItemStack(Material.PAPER).edit {
                addLore("클릭 시 송금이 완료됩니다.")
            },
            onClose = {
            },
            onClickResult = { result ->
                val money = result.text.replace("_", "").toIntOrNull()
                if (money == null) {
                    player.sendMessage("숫자만 입력해주세요.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                if (money <= 0) {
                    player.sendMessage("0보다 큰 금액을 입력해주세요.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                if (player.money < money) {
                    player.sendMessage("돈이 부족합니다.")
                    return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("_"))
                }

                player.money -= money
                target.money += money

                player.sendMessage("송금이 완료되었습니다.")

                if (target.isOnline) {
                    target.sendMessage("${player.name}님으로부터 ${money}원을 받았습니다.")
                }

                return@AnvilInventory listOf(AnvilGUI.ResponseAction.close())
            }
        ).open(player)
    }
}
