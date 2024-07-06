package io.github.hxxniverse.hobeaktown.feature.delivery_service

import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBox
import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBoxes
import io.github.hxxniverse.hobeaktown.feature.delivery_service.ui.DeliveryBoxPreviewUi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.jetbrains.exposed.sql.transactions.transaction

class DeliveryServiceListener : Listener {
    @EventHandler
    fun onRightClickDeliveryBoxItem(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        val deliveryBox = item.getDeliveryBox() ?: return
        val finalDeliveryBox = transaction { DeliveryBox.find { DeliveryBoxes.id eq deliveryBox.id }.first() }

        println("finalDeliveryBox: $finalDeliveryBox")

        if (player.isSneaking) {
            if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
                DeliveryBoxPreviewUi(finalDeliveryBox).open(player)
            } else if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                finalDeliveryBox.open(player)
            }
        }
    }
}