package io.github.hxxniverse.hobeaktown.feature.bee.ui

import io.github.hxxniverse.hobeaktown.feature.bee.Beehive
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BeehiveUi(private val beehive: Beehive) : CustomInventory("벌통", 54) {
    init {
        background(BACKGROUND)

        empty(3 to 3, 4 to 7)

        val idealState = mutableMapOf<Int, ItemStack>()
        for (row in 3..4) {
            for (col in 3..7) {
                val index = (row - 1) * 9 + (col - 1)
                idealState[index] = ItemStack(Material.HONEYCOMB, 1)
            }
        }

        button(5 to 4, 5 to 6, icon {
            type = Material.GREEN_STAINED_GLASS_PANE
            name = "§a양봉 시작".component()
            lore = listOf("".component(), "§7꿀을 1개씩 가득 채우고 누르세요.".component())
        }) {
            val player = it.whoClicked as Player
            val currentState = mutableMapOf<Int, ItemStack>()

            // 현재 상태를 확인하여 currentState 에 저장
            for (row in 3..4) {
                for (col in 3..7) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = it.inventory.getItem(index)

                    if (item != null) {
                        currentState[index] = item
                    }
                }
            }

            // 현재 GUI에 벌집이 1개씩 똑바로 놓아져있지 않은 경우
            if (idealState != currentState) {
                currentState.forEach { (index, item) ->
                    player.inventory.addItem(item)
                    it.inventory.setItem(index, null)
                }

                player.closeInventory()
                player.sendMessage("§6[양봉]§7 모든 슬롯을 벌집 1개씩 채워주세요!")

                return@button
            }

            beehive.start(player.uniqueId)

            player.closeInventory()
            player.sendMessage("§6[양봉]§7 양봉이 시작되었습니다! 1시간이 소요됩니다.")
        }
    }
}