package io.github.hxxniverse.hobeaktown.feature.bee.ui

import io.github.hxxniverse.hobeaktown.feature.bee.Beehive
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import kotlin.random.Random

class BeehiveRewardUi(private val beehive: Beehive) : CustomInventory("벌통", 54) {
    companion object {
        val rewardMap: MutableMap<Int, BeehiveRewardUi> = mutableMapOf()

        fun getOrCreate(beehive: Beehive): BeehiveRewardUi {
            return rewardMap.getOrPut(beehive.id.value) {
                BeehiveRewardUi(beehive)
            }
        }
    }

    init {
        background(BACKGROUND)

        empty(3 to 3, 4 to 7)

        val rewards = Beehive.getRewards(beehive.location)

        // 각 아이템을 60% 확률로 제거
        val filteredRewards = rewards.filter { Random.nextFloat() > 0.6 }

        // 남은 아이템을 해당 칸에 표시
        filteredRewards.forEach { (index, item) ->
            val row = index / 9 + 1
            val col = index % 9 + 1
            item(row to col, item)
        }

        button(5 to 4, 5 to 6, icon {
            type = Material.GREEN_STAINED_GLASS_PANE
            name = "보상 받기".component()
        }) {
            filteredRewards.forEach { (_, item) ->
                it.whoClicked.inventory.addItem(item)
            }

            it.whoClicked.closeInventory()
            it.whoClicked.sendMessage("§6[양봉]§7 양봉 보상이 지급되었습니다!")

            beehive.reset()
            rewardMap.remove(beehive.id.value)
        }
    }
}