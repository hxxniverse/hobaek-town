package io.github.hxxniverse.hobeaktown.feature.real_estate.ui

import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstate
import io.github.hxxniverse.hobeaktown.feature.real_estate.toItemStack
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory

class RealEstateCleanUi(
    realEstate: RealEstate
) : CustomInventory("Real Estate Clean", 27) {
    init {
        inventory {
            // 5,2 - 정보
            button(realEstate.toItemStack(), 5 to 2)
            // 2,3 ~ 3,3 - 취소
            button(CANCEL, 2 to 3, 3 to 3) {
                player.closeInventory()
            }
            // 7,3 ~ 8,3 - 확인
            button(CONFIRM, 7 to 3, 8 to 3) {
                realEstate.clean()
            }
        }
    }
}