package io.github.hxxniverse.hobeaktown.feature.bee.ui

import io.github.hxxniverse.hobeaktown.feature.bee.BeehiveSetup
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class BeehiveSetUi(val code: String) : CustomInventory("벌통 생성", 54) {
    init {
        background(BACKGROUND)

        empty(3 to 3, 4 to 7)

        button(6 to 9, icon {
            type = Material.BEEHIVE
            name = "벌통 생성".component()
        }) {
            if(!it.isLeftClick) return@button

            val rewards: MutableMap<Int, ItemStack> = mutableMapOf()

            // 좌표 (3,3)에서 (4,7) 사이의 아이템들만 가져오기
            for (i in 3..4) {
                for (j in 3..7) {
                    val index = (i - 1) * 9 + (j - 1) // 2차원 [i, j]을 1차원으로 인벤토리 배열 형태로 변환
                    val item = getItem(i to j)
                    if (item != null && item.type != Material.AIR) {
                        rewards[index] = item
                    }
                }
            }

            BeehiveSetup.createSetup(code, rewards)

            it.whoClicked.inventory.addItem(ItemStackBuilder(Material.BEEHIVE).setDisplayName("벌통 생성 블럭: $code").build())
            it.whoClicked.closeInventory()
        }
    }
}
