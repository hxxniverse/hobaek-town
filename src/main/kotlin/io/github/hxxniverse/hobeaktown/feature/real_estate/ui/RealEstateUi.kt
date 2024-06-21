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
            // 2,2 to 8,2 real estates
            for (i in 0 until 7) {
                // if blank is button( ItemStack(Air) else button( ItemStack(RealEstate[i]) )
                println(realEstatePage)
                val realEstates = realEstatePage.get(realEstatePage.getCurrentPageNumber())
                if (i < realEstates.size) {
                    val realEstate = realEstates[i]
                    button(realEstate.toItemStack(), 2 + i to 2) {
                        // open real estate ui
                    }
                }
            }

            // 4,3 previous
            if (realEstatePage.hasPreviousPage()) {
                button(PREVIOUS_PAGE, 4 to 3) {
                    realEstatePage.previousPage()
                }
            }

            // 4,5 next
            if (realEstatePage.hasNextPage()) {
                button(NEXT_PAGE, 6 to 3) {
                    realEstatePage.nextPage()
                }
            }
        }
    }
}