package io.github.hxxniverse.hobeaktown.feature.real_estate.ui

import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstate
import io.github.hxxniverse.hobeaktown.feature.real_estate.toItemStack
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.OfflinePlayer

class RealEstateTransferUi(
    realEstate: RealEstate,
    target: OfflinePlayer
) : CustomInventory("Real Estate Transfer", 27) {
    init {
        inventory {
            // 5,2 - 정보
            button(2 to 5, realEstate.toItemStack())
            // 2,3 ~ 3,3 - 취소
            button(3 to 2, 3 to 3, CANCEL_ICON) {
                player.closeInventory()
            }
            // 7,3 ~ 8,3 - 확인
            button(3 to 7, 3 to 8, CONFIRM_ICON) {
                realEstate.transfer(player, target)
            }
        }
    }
}