package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

/**
 * Member 테이블
 * 컬럼명	데이터 타입	제약 조건
 * uuid	TEXT	PRIMARY KEY NOT NULL
 * name	TEXT	NOT NULL UNIQUE
 * role_id	INTEGER	NOT NULL DEFAULT 1
 * FOREIGN KEY (role_id)	REFERENCES Role (id)	ON UPDATE NO ACTION ON DELETE NO ACTION
 */
object UserKeyCards : UUIDTable() {
    val uuid = uuid("uuid").uniqueIndex()
    val role = reference("role_id", Roles)
}

class UserKeyCard(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<UserKeyCard>(UserKeyCards)

    var uuid by UserKeyCards.uuid
    var role by UserKeyCards.role
}