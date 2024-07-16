package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

/**
 * Keycard 테이블
 * 컬럼명	데이터 타입	제약 조건
 * id	INTEGER	PRIMARY KEY NOT NULL
 * name	TEXT	NOT NULL
 * role_id	INTEGER	NOT NULL
 * FOREIGN KEY (role_id)	REFERENCES Role (id)	ON UPDATE NO ACTION ON DELETE NO ACTION
 */
object KeyCards : IntIdTable() {
    val name = varchar("name", 50)
    val role = reference("role_id", Roles)
}

class KeyCard(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<KeyCard>(KeyCards) {
        fun isExistsKeyCard(name: String, roleName: String): Boolean = loggedTransaction {
            // 직업 아이디 가져오기
            val role = Role.find { Roles.role eq roleName }.firstOrNull() ?: return@loggedTransaction false
            // 키카드 존재 여부 확인
            val isExists = KeyCard.find { (KeyCards.name eq name) and (KeyCards.role eq role.id) }.firstOrNull() != null

            return@loggedTransaction isExists
        }
        fun isExistsKeyName(name: String): Boolean = loggedTransaction {
            val isExists = KeyCard.find { (KeyCards.name eq name) }.firstOrNull() != null
            return@loggedTransaction isExists
        }
    }

    var name by KeyCards.name
    var role by KeyCards.role
}