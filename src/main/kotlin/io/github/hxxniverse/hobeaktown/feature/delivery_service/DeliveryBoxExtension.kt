package io.github.hxxniverse.hobeaktown.feature.delivery_service

import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBox
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction

fun ItemStack.setDeliveryBox(deliveryBox: DeliveryBox) = edit {
    addPersistentData("deliveryBoxId", deliveryBox.id.toString())
}

fun ItemStack.getDeliveryBox(): DeliveryBox? {
    val id = getPersistentData<String>("deliveryBoxId") ?: return null
    return transaction { DeliveryBox.findById(id.toInt()) }
}