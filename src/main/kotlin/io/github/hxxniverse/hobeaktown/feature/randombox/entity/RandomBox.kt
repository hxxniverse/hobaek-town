package io.github.hxxniverse.hobeaktown.feature.randombox.entity

import io.github.hxxniverse.hobeaktown.util.database.itemStack
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

enum class RandomBoxChance(val chance: Double) {
    RED(30.0),
    ORANGE(25.0),
    YELLOW(15.0),
    GREEN(10.0),
    BLUE(8.0),
    PURPLE(6.0),
    BROWN(4.0),
    BLACK(1.5),
    WHITE(0.5)
}

object RandomBoxes : IntIdTable() {
    val name = varchar("name", 50)
    val itemStack = itemStack("item_stack")
}

class RandomBox(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RandomBox>(RandomBoxes)

    var name by RandomBoxes.name
    var itemStack by RandomBoxes.itemStack
    val items by RandomBoxItem referrersOn RandomBoxItems.randomBox

    fun open(player: Player) {
        // 확률에 따라 리스트 생성
        val items = items.map { it.itemStack to it.chance.chance }
        // 확률에 따라 아이템 랜덤 선택
        val item = items.randomByChance { it.second }

        player.inventory.addItem(item)
    }

    fun <T> List<Pair<T, Double>>.randomByChance(selector: (Pair<T, Double>) -> Double): T {
        val sum = sumByDouble { selector(it) }
        val random = Math.random() * sum
        var current = 0.0
        for ((item, chance) in this) {
            current += chance
            if (random <= current) {
                return item
            }
        }
        return first().first
    }
}

object RandomBoxItems : IntIdTable() {
    val randomBox = reference("random_box", RandomBoxes)
    val itemStack = itemStack("item_stack")
    val chance = enumerationByName("chance", 10, RandomBoxChance::class)
}

class RandomBoxItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RandomBoxItem>(RandomBoxItems)

    var randomBox by RandomBox referencedOn RandomBoxItems.randomBox
    var itemStack by RandomBoxItems.itemStack
    var chance by RandomBoxItems.chance
}