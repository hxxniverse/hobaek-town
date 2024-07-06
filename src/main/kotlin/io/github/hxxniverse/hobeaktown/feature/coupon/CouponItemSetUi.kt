package io.github.hxxniverse.hobeaktown.feature.coupon

import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material

class CouponItemSetUi(
    private val coupon: Coupon
) : CustomInventory("", 54) {
    init {
        inventory {
            background(BACKGROUND)

            empty(2 to 2, 5 to 8)

            button(6 to 5, icon {
                name = "저장".component()
                lore = listOf("저장".component())
                type = Material.GLASS_PANE
            }) {
                coupon.items.forEach(CouponItem::delete)
                getItems(2 to 2, 5 to 8).filterNotNull().forEach {
                    CouponItem.new {
                        this.coupon = coupon
                        this.item = it
                    }
                }
                it.whoClicked.closeInventory()
                player.sendMessage("쿠폰 아이템이 설정되었습니다.")
            }
        }
    }
}