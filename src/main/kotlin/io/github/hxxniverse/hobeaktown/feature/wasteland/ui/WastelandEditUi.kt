package io.github.hxxniverse.hobeaktown.feature.wasteland.ui

import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.Wasteland
import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.WastelandSetup
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class WastelandEditUi(val code: String) : CustomInventory("황무지 보상 수정 [$code]", 54) {
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
        // Setup UI 구조를 동일하게 배치
        items.forEachIndexed { index, (material, name) ->
            item(1 to (index + 1), ItemStackBuilder(material).setDisplayName(name).build())
            item(4 to (index + 1), ItemStackBuilder(material).setDisplayName(name).build())
        }

        display(3 to 1, 3 to 9, ItemStackBuilder(Material.GLASS_PANE).setDisplayName("").build())
        display(6 to 1, 6 to 9, ItemStackBuilder(Material.GLASS_PANE).setDisplayName("").build())

        empty(2 to 1, 2 to 9)
        empty(5 to 1, 5 to 9)

        val rewards = WastelandSetup.getItemsByCode(code)

        rewards.forEach { (index, item) ->
            val row = index / 9 + 1
            val col = index % 9 + 1
            item(row to col, item) {
                it.isCancelled = false
            }
        }

        button(6 to 7, ItemStackBuilder(Material.SUSPICIOUS_GRAVEL).setDisplayName("자갈 블럭 생성").build()) {
            val newRewards = mutableMapOf<Int, ItemStack>()

            for (row in 2..2) {
                for (col in 1..9) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = getItem(row to col)
                    if (item != null && item.type != Material.AIR) {
                        newRewards[index] = item
                    }
                }
            }

            for (row in 5..5) {
                for (col in 1..9) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = getItem(row to col)
                    if (item != null && item.type != Material.AIR) {
                        newRewards[index] = item
                    }
                }
            }

            Wasteland.editRewards(code, newRewards)

            player.inventory.addItem(ItemStackBuilder(Material.SUSPICIOUS_GRAVEL).setDisplayName("황무지 설정 블럭 - $code").build())
            player.closeInventory()

            player.sendMessage("§6[황무지]§7 황무지 보상 아이템이 수정되었습니다: $code")
            player.sendMessage("§6[황무지]§7 새로운 황무지 생성 블럭을 지급하였습니다. (기존 블럭도 보상 변경됨)")
        }

        button(6 to 9, ItemStackBuilder(Material.SUSPICIOUS_SAND).setDisplayName("모래 블럭 생성").build()) {
            val newRewards = mutableMapOf<Int, ItemStack>()

            for (row in 2..2) {
                for (col in 1..9) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = getItem(row to col)
                    if (item != null && item.type != Material.AIR) {
                        newRewards[index] = item
                    }
                }
            }

            for (row in 5..5) {
                for (col in 1..9) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = getItem(row to col)
                    if (item != null && item.type != Material.AIR) {
                        newRewards[index] = item
                    }
                }
            }

            Wasteland.editRewards(code, newRewards)

            player.inventory.addItem(ItemStackBuilder(Material.SUSPICIOUS_SAND).setDisplayName("황무지 설정 블럭 - $code").build())
            player.closeInventory()

            player.sendMessage("§6[황무지]§7 황무지 보상 아이템이 수정되었습니다: $code")
            player.sendMessage("§6[황무지]§7 새로운 황무지 생성 블럭을 지급하였습니다. (기존 블럭도 보상 변경됨)")
        }
    }
}