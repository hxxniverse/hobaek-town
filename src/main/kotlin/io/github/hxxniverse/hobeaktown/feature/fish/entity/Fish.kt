package io.github.hxxniverse.hobeaktown.feature.fish.entity

import io.github.hxxniverse.hobeaktown.util.database.itemStack
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import kotlin.random.Random

object Fishes : IntIdTable() {
    val tier = integer("tier") // 등급 [ 1등급=1, 2등급=2, 3등급=3, 레전더리=10 ]
    val item = itemStack("item")
}

class Fish(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Fish>(Fishes) {
        fun addFish(item: ItemStack, tier: Int) {
            loggedTransaction {
                Fish.new {
                    this.item = item
                    this.tier = tier
                }
            }
        }

        fun removeFish(item: ItemStack) {
            loggedTransaction {
                Fish.find {
                    Fishes.item eq item
                }.forEach { it.delete() }
            }
        }

        fun getFishes(tier: Int): List<Fish> {
            return loggedTransaction {
                Fish.find {
                    Fishes.tier eq tier
                }.toList()
            }
        }

        fun getTier(itemStack: ItemStack): Int? {
            return loggedTransaction {
                Fish.find {
                    Fishes.item eq itemStack
                }.firstOrNull()?.tier
            }
        }

        fun editFishes(tier: Int, newItems: List<ItemStack>) {
            loggedTransaction {
                val existingFishes = Fish.find { Fishes.tier eq tier }.toList()
                val existingItems = existingFishes.map { it.item }
                val adds = newItems.filter { it !in existingItems }
                val removes = existingItems.filter { it !in newItems }

                adds.forEach { itemStack ->
                    Fish.new {
                        this.item = itemStack
                        this.tier = tier
                    }
                }

                removes.forEach { itemStack ->
                    Fish.find { Fishes.item eq itemStack }.forEach { it.delete() }
                }
            }
        }

        fun randomFish(rodLevel: Int): ItemStack {
            return loggedTransaction {
                // 0.001% 확률로 레전더리 물고기 리턴
                val legendaryFish = Fish.find { Fishes.tier eq 10 }.toList()
                if (legendaryFish.isNotEmpty() && Random.nextDouble() <= 0.00001) {
                    return@loggedTransaction legendaryFish.random().item
                }

                val tier1 = Fish.find { Fishes.tier eq 1 }.toList()
                val tier2 = Fish.find { Fishes.tier eq 2 }.toList()
                val tier3 = Fish.find { Fishes.tier eq 3 }.toList()

                val fishes = when(rodLevel) {
                    1 -> tier1 + tier2 + tier3
                    2 -> tier1.shuffled().take(tier1.size / 2) + tier2 + tier3
                    3 -> tier2 + tier3
                    4 -> tier2.shuffled().take(tier2.size / 2) + tier3
                    5 -> tier3
                    else -> throw IllegalArgumentException("잘못된 낚싯대 레벨이 입력됨")
                }

                if(fishes.isEmpty()) {
                    // 버그 방지용 (이론상 불가능)
                    return@loggedTransaction ItemStack(Material.AIR)
                }

                return@loggedTransaction fishes.random().item
            }
        }
    }

    var tier by Fishes.tier
    var item by Fishes.item
}