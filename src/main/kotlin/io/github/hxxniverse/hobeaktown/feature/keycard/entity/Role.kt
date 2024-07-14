package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Role 테이블
 * 컬럼명	데이터 타입	제약 조건
 * id	INTEGER	PRIMARY KEY NOT NULL
 * name	TEXT
 */
object Roles : IntIdTable() {
    val role = varchar("role", 50).uniqueIndex()
}

class Role(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<Role>(Roles) {
        fun isExistsRole(role: String): Boolean = transaction {
            return@transaction Role.find { Roles.role eq role }.firstOrNull() != null
        }
        fun initialize() = transaction {
            val defaultRoles: Array<String> = arrayOf("시민", "경찰", "회사원", "은행원", "국회의원", "군인", "훈련병", "사업가", "VIP")
            defaultRoles.forEach { role ->
                if (!isExistsRole(role)) {
                    Role.new {
                        this.role = role
                    }
                }
            }
        }
        fun getId(role: String): Int {
            return transaction {
                val roleEntity = Role.find { Roles.role eq role }.firstOrNull()
                    ?: error("역할 '$role'을(를) 찾을 수 없습니다.")
                roleEntity.id.value
            }
        }
    }
    var role by Roles.role
}