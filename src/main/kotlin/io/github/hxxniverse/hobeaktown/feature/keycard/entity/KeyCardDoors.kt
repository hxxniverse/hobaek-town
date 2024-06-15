package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import io.github.hxxniverse.hobeaktown.util.database.location
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

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
    val name = varchar("name", 50)
    val location = location("location")
    val permission = varchar("permission", 50)
}

class KeyCardDoor(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<KeyCardDoor>(KeyCardDoors)

    var name by KeyCardDoors.name
    var location by KeyCardDoors.location
    var permission by KeyCardDoors.permission
}