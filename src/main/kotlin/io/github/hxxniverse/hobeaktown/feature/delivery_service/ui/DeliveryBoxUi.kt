package io.github.hxxniverse.hobeaktown.feature.delivery_service.ui

import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.PlayerDeliveryBox
import io.github.hxxniverse.hobeaktown.util.EntityListPager
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class DeliveryBoxUi : CustomInventory("택배함", 54) {

    private val boxes = EntityListPager(PlayerDeliveryBox)

    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

            // previous page button
            if (boxes.hasPreviousPage()) {
                button(6 to 1, 6 to 2, PREVIOUS_PAGE) {
                    boxes.previousPage()
                    update()
                }
            }

            // next page button
            if (boxes.hasNextPage()) {
                button(6 to 7, 6 to 8, NEXT_PAGE) {
                    boxes.nextPage()
                    update()
                }
            }

            // box list 2,2 ~ 5,8 before empty
            empty(2 to 2, 5 to 8)
            boxes.getCurrentPage().forEachIndexed { index, box ->
                val row = index / 7
                val col = index % 7
                button(2 + col to 2 + row, 2 + col to 2 + row, icon(box.itemStack) {
                    name = Bukkit.getOfflinePlayer(box.sender).name?.component() ?: "".component()
                    lore = listOf(box.itemStack.itemMeta.lore?.get(0) ?: "").map { it.component() }
                }) {
                    // open box
                }
            }
        }
    }
}