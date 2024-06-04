package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.util.database.location
import org.bukkit.Location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object RealEstates : IntIdTable() {
    val name = varchar("name", 255)
    val price = integer("price")
    val due = integer("due")
    val type = enumeration("type", RealEstateType::class)
    val owner = uuid("owner")
    val pos1 = location("pos1")
    val pos2 = location("pos2")
    val grade = enumeration("grade", RealEstateGrade::class)
}

class RealEstate(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<RealEstate>(RealEstates) {
        fun create(
            name: String,
            price: Int,
            due: Int,
            type: RealEstateType,
            owner: UUID,
            pos1: Location,
            pos2: Location,
            grade: RealEstateGrade
        ) = transaction {
            new {
                this.name = name
                this.price = price
                this.due = due
                this.type = type
                this.owner = owner
                this.pos1 = pos1
                this.pos2 = pos2
                this.grade = grade
            }
        }
    }

    var name by RealEstates.name
    var price by RealEstates.price
    var due by RealEstates.due
    var type by RealEstates.type
    var owner by RealEstates.owner
    var pos1 by RealEstates.pos1
    var pos2 by RealEstates.pos2
    var grade by RealEstates.grade
}

enum class RealEstateType {
    NORMAL,
    LAND
}

enum class RealEstateGrade(
    val yLimit: Int
) {
    C(5),
    B(10),
    A(14),
    S(25),
    R(40)
}
