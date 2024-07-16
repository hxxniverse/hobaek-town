package io.github.hxxniverse.hobeaktown.feature.delivery_service.entity

import io.github.hxxniverse.hobeaktown.util.database.itemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

object DeliveryBoxes : IntIdTable() {
    val name = varchar("name", 255)
    val boxItem = itemStack("box_item")
}

class DeliveryBox(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DeliveryBox>(DeliveryBoxes)

    var name by DeliveryBoxes.name
    var boxItem by DeliveryBoxes.boxItem

    val items by DeliveryBoxItem referrersOn DeliveryBoxItems.box

    fun open(player: Player) {
        items.forEach { player.inventory.addItem(it.item) }
    }

    fun addItem(item: ItemStack) = loggedTransaction {
        DeliveryBoxItem.new {
            this.box = this@DeliveryBox
            this.item = item
        }
    }

    fun removeItem(item: ItemStack) {
        loggedTransaction {
            DeliveryBoxItem.find { DeliveryBoxItems.box eq this@DeliveryBox.id and (DeliveryBoxItems.item eq item) }
                .forEach { it.delete() }
        }
    }

    fun clearItems() = loggedTransaction {
        DeliveryBoxItem.find { DeliveryBoxItems.box eq this@DeliveryBox.id }.forEach { it.delete() }
    }

    override fun toString(): String {
        return "DeliveryBox(name='$name', boxItem=$boxItem, items=$items)"
    }
}

object DeliveryBoxItems : IntIdTable() {
    val box = reference("box", DeliveryBoxes)
    val item = itemStack("item")
}

class DeliveryBoxItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DeliveryBoxItem>(DeliveryBoxItems)

    var box by DeliveryBox referencedOn DeliveryBoxItems.box
    var item by DeliveryBoxItems.item
}