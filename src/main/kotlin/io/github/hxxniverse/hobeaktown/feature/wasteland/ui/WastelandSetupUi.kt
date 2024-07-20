package io.github.hxxniverse.hobeaktown.feature.wasteland.ui

import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.WastelandSetup
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WastelandSetupUi(private val code: String) : CustomInventory("황무지 블럭제작", 54) {
    init {
        item(1 to 1, ItemStackBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("30%").build())
        item(1 to 2, ItemStackBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplayName("25%").build())
        item(1 to 3, ItemStackBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("15%").build())
        item(1 to 4, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("10%").build())
        item(1 to 5, ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("8%").build())
        item(1 to 6, ItemStackBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("6%").build())
        item(1 to 7, ItemStackBuilder(Material.BROWN_STAINED_GLASS_PANE).setDisplayName("4%").build())
        item(1 to 8, ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("1.5%").build())
        item(1 to 9, ItemStackBuilder(Material.WHITE_STAINED_GLASS_PANE).setDisplayName("0.5%").build())

        item(4 to 1, ItemStackBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("30%").build())
        item(4 to 2, ItemStackBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplayName("25%").build())
        item(4 to 3, ItemStackBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("15%").build())
        item(4 to 4, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("10%").build())
        item(4 to 5, ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("8%").build())
        item(4 to 6, ItemStackBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("6%").build())
        item(4 to 7, ItemStackBuilder(Material.BROWN_STAINED_GLASS_PANE).setDisplayName("4%").build())
        item(4 to 8, ItemStackBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("1.5%").build())
        item(4 to 9, ItemStackBuilder(Material.WHITE_STAINED_GLASS_PANE).setDisplayName("0.5%").build())

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
            player.sendMessage("황무지용 자갈 블럭이 생성되었습니다: $code")
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
            player.sendMessage("황무지용 모래 블럭이 생성되었습니다: $code")
        }
    }
}