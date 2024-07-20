package io.github.hxxniverse.hobeaktown.feature.bee.ui

import io.github.hxxniverse.hobeaktown.feature.bee.Beehive
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material

class BeehiveWaitingUi(private val beehive: Beehive) : CustomInventory("벌통", 54) {
    init {
        background(BACKGROUND)

        display(3 to 4, 4 to 6, ItemStackBuilder(Material.CLOCK)
            .setDisplayName("진행중")
            .addLore("")
            .addLore("" + beehive.startTime + " 에 시작함")
            .build())
    }
}