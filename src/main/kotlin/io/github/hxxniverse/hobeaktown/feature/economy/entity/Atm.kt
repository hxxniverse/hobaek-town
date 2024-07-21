package io.github.hxxniverse.hobeaktown.feature.economy.entity

import io.github.hxxniverse.hobeaktown.util.database.location
import org.bukkit.Location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

object Atms : IntIdTable() {
    val location = location("location")
    val fee = integer("fee").default(0)
}

class Atm(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Atm>(Atms) {
        // find by location
        fun findByLocation(location: Location) = loggedTransaction {
            find { Atms.location eq location }.firstOrNull()
        }

        // create
        fun create(location: Location) = loggedTransaction {
            new {
                this.location = location
            }
        }
    }

    var location by Atms.location
    var fee by Atms.fee
}

