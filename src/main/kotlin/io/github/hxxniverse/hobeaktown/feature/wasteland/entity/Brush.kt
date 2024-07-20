package io.github.hxxniverse.hobeaktown.feature.wasteland.entity

import io.github.hxxniverse.hobeaktown.util.database.itemStack
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Brushes : IntIdTable() {
    val level = integer("level")
    val item = itemStack("itemstack")
}

class Brush(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Brush>(Brushes) {
        fun registerBrush(item: ItemStack, level: Int) {
            loggedTransaction {
                Brush.new {
                    this.item = item
                    this.level = level
                }
            }
        }

        fun unregisterBrush(item: ItemStack) {
            loggedTransaction {
                Brush.find {
                    Brushes.item eq item
                }.forEach { it.delete() }
            }
        }

        fun getRegisteredBrushes(): List<Brush> {
            return loggedTransaction {
                Brush.all().toList()
            }
        }
    }

    var level by Brushes.level
    var item by Brushes.item
}