package io.github.hxxniverse.hobeaktown.feature.fish.entity

import io.github.hxxniverse.hobeaktown.util.database.itemStack
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object FishingRods : IntIdTable() {
    val item = itemStack("item")
    val level = integer("level")
}

class FishingRod(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FishingRod>(FishingRods) {
        fun addFishingRod(item: ItemStack, level: Int) {
            loggedTransaction {
                FishingRod.new {
                    this.item = item
                    this.level = level
                }
            }
        }

        fun removeFishingRod(item: ItemStack) {
            loggedTransaction {
                FishingRod.find {
                    FishingRods.item eq item
                }.forEach { it.delete() }
            }
        }

        fun getFishingRods(): List<FishingRod> {
            return loggedTransaction {
                FishingRod.all().toList()
            }
        }

        fun getByItemStack(itemStack: ItemStack): FishingRod? {
            return loggedTransaction {
                FishingRod.find {
                    FishingRods.item eq itemStack.clone().apply {
                        durability = 0
                    }
                }.firstOrNull()
            }
        }
    }

    var item by FishingRods.item
    var level by FishingRods.level
}