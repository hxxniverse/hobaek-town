package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import io.github.hxxniverse.hobeaktown.util.database.location
import org.bukkit.Location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Doors 테이블
 * 컬럼명	데이터 타입	제약 조건
 * id	INTEGER	PRIMARY KEY AUTOINCREMENT
 * name	TEXT	NOT NULL
 * x	DOUBLE	NOT NULL
 * y	DOUBLE	NOT NULL
 * z	DOUBLE	NOT NULL
 * permission	TEXT	NOT NULL
 */
object KeyCardDoors : IntIdTable() {
    val location = location("location")
    val name = varchar("name", 50)
}

class KeyCardDoor(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<KeyCardDoor>(KeyCardDoors) {
        fun insertDoorData(location: Location, name: String) {
            transaction {
                val baseLocation = location.clone().apply { y = 0.0 }
                KeyCardDoor.new {
                    this.location = baseLocation
                    this.name = name
                }
            }
        }
        fun delete(location: Location) {
            transaction {
                val baseLocation = location.clone().apply { y = 0.0 }
                KeyCardDoor.find {
                    KeyCardDoors.location eq baseLocation
                }.forEach { it.delete() }
            }
        }
        fun checkName(location: Location, name: String) = transaction {
            val baseLocation = location.clone().apply { y = 0.0 }
            return@transaction KeyCardDoor.find {
                (KeyCardDoors.location eq baseLocation) and
                        (KeyCardDoors.name eq name)
            }.count() > 0;
        }
    }
    var location by KeyCardDoors.location
    var name by KeyCardDoors.name
}