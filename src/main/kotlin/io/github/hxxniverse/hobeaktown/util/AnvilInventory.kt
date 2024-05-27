package io.github.hxxniverse.hobeaktown.util

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class AnvilInventory(
    title: String,
    text: String,
    itemInputLeft: ItemStack = ItemStack(Material.AIR),
    itemInputRight: ItemStack = ItemStack(Material.AIR),
    itemOutput: ItemStack = ItemStack(Material.AIR),
    onClose: (AnvilGUI.StateSnapshot) -> Unit = {},
    private val onClickLeft: (AnvilGUI.StateSnapshot) -> List<AnvilGUI.ResponseAction> = { emptyList() },
    private val onClickRight: (AnvilGUI.StateSnapshot) -> List<AnvilGUI.ResponseAction> = { emptyList() },
    private val onClickResult: (AnvilGUI.StateSnapshot) -> List<AnvilGUI.ResponseAction> = { emptyList() },
) {
    private val builder: AnvilGUI.Builder = AnvilGUI.Builder()
        .onClose(onClose)
        .onClick { slot, snapshot ->
            if (slot == AnvilGUI.Slot.INPUT_LEFT) {
                return@onClick onClickLeft(snapshot)
            }
            if (slot == AnvilGUI.Slot.INPUT_RIGHT) {
                return@onClick onClickRight(snapshot)
            }
            return@onClick onClickResult(snapshot)
        }
        .title(title)
        .text(text)
        .itemLeft(itemInputLeft)
        .itemRight(itemInputRight)
        .itemOutput(itemOutput)
        .plugin(plugin)

    fun open(player: Player) {
        builder.open(player)
    }
}