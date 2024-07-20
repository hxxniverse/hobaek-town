package io.github.hxxniverse.hobeaktown.feature.bee.ui

import io.github.hxxniverse.hobeaktown.feature.bee.Beehive
import io.github.hxxniverse.hobeaktown.feature.bee.BeehiveSetup
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class BeehiveEditUi(val code: String) : CustomInventory("벌통 수정", 54) {
    init {
        background(BACKGROUND)

        empty(3 to 3, 4 to 7)

        // 해당 code에 알맞는 보상을 불러오기
        val rewards = BeehiveSetup.getItemsByCode(code)

        // 불러온 보상을 UI에 배치
        rewards.forEach { (index, item) ->
            val row = index / 9 + 1
            val col = index % 9 + 1
            item(row to col, item) {
                it.isCancelled = false
            }
        }

        button(6 to 9, icon {
            type = Material.BEEHIVE
            name = "벌통 수정".component()
        }) {
            val newRewards = mutableMapOf<Int, ItemStack>()

            // 3,3부터 4,7 사이의 아이템을 새로운 보상으로 저장
            for (row in 3..4) {
                for (col in 3..7) {
                    val index = (row - 1) * 9 + (col - 1)
                    val item = getItem(row to col)
                    if (item != null && item.type != Material.AIR) {
                        newRewards[index] = item
                    }
                }
            }

            // 보상을 수정
            Beehive.editRewards(code, newRewards)

            it.whoClicked.closeInventory()
            it.whoClicked.sendMessage("§6[양봉]§7 보상이 성공적으로 수정되었습니다.")
        }
    }
}
