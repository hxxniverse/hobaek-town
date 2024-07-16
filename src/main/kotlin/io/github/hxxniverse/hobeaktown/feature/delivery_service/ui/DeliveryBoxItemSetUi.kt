package io.github.hxxniverse.hobeaktown.feature.delivery_service.ui

import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBox
import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBoxItem
import io.github.hxxniverse.hobeaktown.feature.delivery_service.setDeliveryBox
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class DeliveryBoxItemSetUi(
    deliveryBox: DeliveryBox
) : CustomInventory("택배상자 제작", 54) {
    init {
        inventory {
            background(BACKGROUND)

            // box list 2,2 ~ 5,8 before empty
            empty(2 to 2, 5 to 8)

            // load items
            deliveryBox.items.forEachIndexed { index, item ->
                val row = index / 7 + 2
                val col = index % 7 + 2
                item(row to col, item.item)
            }

            // set box
            item(6 to 5, deliveryBox.boxItem)

            // 6,9 green stained glass pane
            button(6 to 9, CONFIRM_ICON) {

                loggedTransaction {
                    val newDeliveryBox = getItem(6 to 5)

                    if (newDeliveryBox == null) {
                        player.sendMessage("택배상자 아이템이 설정되지 않았습니다.")
                        return@loggedTransaction
                    }

                    deliveryBox.boxItem = newDeliveryBox.setDeliveryBox(deliveryBox)
                }

                // remove all delivery box items
                deliveryBox.clearItems()
                // add item to delivery box from 2,2 to 5,8
                for (row in 2..5) {
                    for (col in 2..8) {
                        val item = getItem(row to col) ?: continue
                        println("deliveryBoxItem: $item")
                        loggedTransaction {
                            DeliveryBoxItem.new {
                                this.box = deliveryBox
                                this.item = item
                            }
                        }
                    }
                }

                player.sendMessage("택배상자 아이템이 설정되었습니다.")
                player.closeInventory()
            }
        }
    }
}