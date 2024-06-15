package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * Role 테이블
 * 컬럼명	데이터 타입	제약 조건
 * id	INTEGER	PRIMARY KEY NOT NULL
 * name	TEXT
 */
object Roles : IntIdTable() {
    val name = varchar("name", 50)
}

class Role(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Role>(Roles) {
        // is exists role
        fun isExistsRole(role: String): Boolean {
            return Role.find { Roles.name eq role }.firstOrNull() != null
        }
    }

    var name by Roles.name
}