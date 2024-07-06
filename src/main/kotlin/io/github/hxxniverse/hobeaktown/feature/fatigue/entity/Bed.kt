package io.github.hxxniverse.hobeaktown.feature.fatigue.entity

import org.bukkit.Material
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object Beds: IntIdTable() {
    val color = varchar("color", 50)
    val cycle = integer("cycle")
    val fatigue = integer("fatigue")
}

class Bed(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Bed>(Beds) {
        private fun isExistsColor(color: String): Boolean = transaction {
            return@transaction Bed.find { Beds.color eq color }.firstOrNull()  != null
        }
        fun initial() = transaction {
            val colorList: List<Pair<String, Pair<Int, Int>>> = listOf(
                Pair(Material.RED_BED.toString(), Pair(60, 2)),
                Pair(Material.ORANGE_BED.toString(), Pair(45, 2)),
                Pair(Material.YELLOW_BED.toString(), Pair(35, 2)),
                Pair(Material.LIME_BED.toString(), Pair(30, 3)),
                Pair(Material.LIGHT_BLUE_BED.toString(), Pair(20, 3)),
                Pair(Material.BLUE_BED.toString(), Pair(15, 4)),
                Pair(Material.PURPLE_BED.toString(), Pair(10, 5))
            )
            colorList.forEach { (color, properties) ->
                if(!isExistsColor(color)) {
                    Bed.new {
                        this.color = color
                        this.cycle = properties.first
                        this.fatigue = properties.second
                    }
                }
            }
        }
    }

    var color by Beds.color
    var cycle by Beds.cycle
    var fatigue by Beds.fatigue
}