package io.github.hxxniverse.hobeaktown.feature.delivery_service.ui

import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBox
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory

class DeliveryBoxPreviewUi(
    deliveryBox: DeliveryBox
) : CustomInventory("택배상자 아이템 목록", 54) {
    init {
        inventory {
            background(BACKGROUND)

            // box list 2,2 ~ 5,8 before empty
            empty(2 to 2, 5 to 8)

            println("boxItem: $deliveryBox")

            // load items
            deliveryBox.items.toList().forEachIndexed { index, item ->
                println("index: $index, item: ${item.item}")
                val row = index / 7 + 2
                val col = index % 7 + 2
                item(row to col, item.item)
            }

            // set box
            item(6 to 5, deliveryBox.boxItem) {
                it.isCancelled = true
            }
        }
    }
}