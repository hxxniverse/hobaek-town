package io.github.hxxniverse.hobeaktown.feature.delivery_service.entity

import io.github.hxxniverse.hobeaktown.util.database.itemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object PlayerDeliveryBoxes : IntIdTable() {
    val sender = uuid("sender")
    val receiver = uuid("recipient")
    val itemStack = itemStack("item_stack")
}

class PlayerDeliveryBox(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerDeliveryBox>(PlayerDeliveryBoxes)

    var sender by PlayerDeliveryBoxes.sender
    var receiver by PlayerDeliveryBoxes.receiver
    var itemStack by PlayerDeliveryBoxes.itemStack
}

fun Player.sendItemStack(sender: Player, itemStack: ItemStack) {
    PlayerDeliveryBox.new {
        this.sender = sender.uniqueId
        this.receiver = uniqueId
        this.itemStack = itemStack
    }
}