package io.github.hxxniverse.hobeaktown.feature.real_estate.ui

import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstate
import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstates
import io.github.hxxniverse.hobeaktown.feature.real_estate.toItemStack
import io.github.hxxniverse.hobeaktown.util.EntityListPager
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class RealEstateUi : CustomInventory("부동산", 27) {

    private val realEstatePage = EntityListPager(RealEstate, 7, condition = { RealEstates.owner eq player.uniqueId })

    init {
        inventory {
            background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))
            for (i in 0 until 7) {
                val realEstates = realEstatePage.get(realEstatePage.getCurrentPageNumber())
                if (i < realEstates.size) {
                    val realEstate = realEstates[i]
                    button(2 + i to 2, realEstate.toItemStack()) {
                        // open real estate ui
                    }
                }
            }

            // 4,3 previous
            if (realEstatePage.hasPreviousPage()) {
                button(3 to 4, PREVIOUS_PAGE_ICON) {
                    realEstatePage.previousPage()
                }
            }

            // 4,5 next
            if (realEstatePage.hasNextPage()) {
                button(3 to 6, NEXT_PAGE_ICON) {
                    realEstatePage.nextPage()
                }
            }
        }
    }
}