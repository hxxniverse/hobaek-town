package io.github.hxxniverse.hobeaktown.feature.wasteland.ui

import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.WastelandSetup
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WastelandSetupUi(private val code: String) : CustomInventory("황무지 블럭제작", 54) {
    companion object {
        val items = listOf(
            Material.RED_STAINED_GLASS_PANE to "30%",
            Material.ORANGE_STAINED_GLASS_PANE to "25%",
            Material.YELLOW_STAINED_GLASS_PANE to "15%",
            Material.GREEN_STAINED_GLASS_PANE to "10%",
            Material.BLUE_STAINED_GLASS_PANE to "8%",
            Material.PURPLE_STAINED_GLASS_PANE to "6%",
            Material.BROWN_STAINED_GLASS_PANE to "4%",
            Material.BLACK_STAINED_GLASS_PANE to "1.5%",
            Material.WHITE_STAINED_GLASS_PANE to "0.5%"
        )
    }

    init {
        items.forEachIndexed { index, (material, name) ->
            item(1 to (index + 1), ItemStackBuilder(material).setDisplayName(name).build())
            item(4 to (index + 1), ItemStackBuilder(material).setDisplayName(name).build())
        }

        display(3 to 1, 3 to 9, ItemStackBuilder(Material.GLASS_PANE).setDisplayName("").build())
        display(6 to 1, 6 to 9, ItemStackBuilder(Material.GLASS_PANE).setDisplayName("").build())

        empty(2 to 1, 2 to 9)
        empty(5 to 1, 5 to 9)

        button(6 to 7, ItemStackBuilder(Material.GRAVEL).setDisplayName("자갈 블럭 생성").build()) {
            val rewards: MutableMap<Int, ItemStack> = mutableMapOf()

            for (row in 2..2) {
                for (col in 1..9) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = getItem(row to col)
                    if(item != null && item.type != Material.AIR) {
                        rewards[index] = item
                    }
                }
            }

            for (row in 5..5) {
                for (col in 1..9) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = getItem(row to col)
                    if(item != null && item.type != Material.AIR) {
                        rewards[index] = item
                    }
                }
            }

            WastelandSetup.createSetup(code, Material.GRAVEL, rewards)

            player.inventory.addItem(ItemStackBuilder(Material.GRAVEL).setDisplayName("황무지 설정 블럭 - $code").build())
            player.closeInventory()
            player.sendMessage("§6[황무지]§7 황무지용 자갈 블럭이 생성되었습니다: $code")
        }

        button(6 to 9, ItemStackBuilder(Material.SAND).setDisplayName("모래 블럭 생성").build()) {
            val rewards: MutableMap<Int, ItemStack> = mutableMapOf()

            for (row in 2..2) {
                for (col in 1..9) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = getItem(row to col)
                    if(item != null && item.type != Material.AIR) {
                        rewards[index] = item
                    }
                }
            }

            for (row in 5..5) {
                for (col in 1..9) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = getItem(row to col)
                    if(item != null && item.type != Material.AIR) {
                        rewards[index] = item
                    }
                }
            }

            WastelandSetup.createSetup(code, Material.SAND, rewards)

            player.inventory.addItem(ItemStackBuilder(Material.SAND).setDisplayName("황무지 설정 블럭 - $code").build())
            player.closeInventory()
            player.sendMessage("§6[황무지]§7 황무지용 모래 블럭이 생성되었습니다: $code")
        }
    }
}