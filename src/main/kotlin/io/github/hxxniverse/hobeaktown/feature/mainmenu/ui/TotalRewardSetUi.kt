package io.github.hxxniverse.hobeaktown.feature.mainmenu.ui

import io.github.hxxniverse.hobeaktown.feature.mainmenu.entity.TotalReward
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class TotalRewardSetUi : CustomInventory("누적 보상 설정", 54) {
    private val positions = listOf(
        2 to 2, 2 to 4, 2 to 6, 2 to 8,
        4 to 2, 4 to 4, 4 to 6, 4 to 8
    )

    init {
        background(BACKGROUND)

        for((index, pos) in positions.withIndex()) {
            val reward = TotalReward.getReward(index)
            item(pos, reward) {
                it.isCancelled = false
            }
        }

        button(6 to 9, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("적용").build()) {
            val player = this.player

            loggedTransaction {
                for((index, pos) in positions.withIndex()) {
                    val item = getItem(pos) ?: ItemStack(Material.AIR)
                    TotalReward.setReward(index, if (item.type == Material.AIR) null else item)
                }
            }

            player.sendMessage("누적 보상이 성공적으로 적용되었습니다.")
        }
    }
}