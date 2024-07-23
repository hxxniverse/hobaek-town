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
    val name = text("name")
    val item = itemStack("itemstack")
}

class Brush(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Brush>(Brushes) {
        fun registerBrush(name: String, item: ItemStack, level: Int) {
            loggedTransaction {
                Brush.new {
                    this.name = name
                    this.item = item
                    this.level = level
                }
            }
        }

        fun unregisterBrush(name: String) {
            loggedTransaction {
                Brush.find {
                    Brushes.name eq name
                }.forEach { it.delete() }
            }
        }

        fun getRegisteredBrushes(): List<Brush> {
            return loggedTransaction {
                Brush.all().toList()
            }
        }

        fun getByName(name: String): Brush? {
            return loggedTransaction {
                Brush.find {
                    Brushes.name eq name
                }.firstOrNull()
            }
        }

        fun getByItemStack(itemStack: ItemStack): Brush? {
            return loggedTransaction {
                Brush.find { Brushes.item eq itemStack }.firstOrNull()
            }
        }
    }

    var level by Brushes.level
    var name by Brushes.name
    var item by Brushes.item
}