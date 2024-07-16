package io.github.hxxniverse.hobeaktown.feature.delivery_service

import com.mojang.authlib.yggdrasil.response.User
import io.github.hxxniverse.hobeaktown.feature.delivery_service.entity.DeliveryBox
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import org.bukkit.inventory.ItemStack
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

fun ItemStack.setDeliveryBox(deliveryBox: DeliveryBox) = edit {
    addPersistentData("deliveryBoxId", deliveryBox.id.toString())
}

fun ItemStack.getDeliveryBox(): DeliveryBox? {
    val id = getPersistentData<String>("deliveryBoxId") ?: return null
    return loggedTransaction { DeliveryBox.findById(id.toInt()) }
}

// 우편 전송