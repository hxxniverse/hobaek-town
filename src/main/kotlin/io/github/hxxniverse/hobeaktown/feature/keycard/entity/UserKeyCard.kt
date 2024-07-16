package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.update
import java.sql.SQLException
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
    val role = reference("role_id", Roles)
}

class UserKeyCard(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserKeyCard>(UserKeyCards) {
        @Throws(SQLException::class)
        fun isExists(player: UUID): Boolean = loggedTransaction {
            return@loggedTransaction UserKeyCard.find { UserKeyCards.id eq player }.firstOrNull() != null;
        }

        @Throws(SQLException::class)
        fun updateMemberRole(player: UUID, roleName: String) = loggedTransaction {
            val playerEntity = UserKeyCard.find { UserKeyCards.id eq player }.firstOrNull()
                ?: throw SQLException("플레이어를 찾을 수 없습니다.")

            val roleId = Role.find { Roles.role eq roleName }
                .singleOrNull()?.id?.value
                ?: throw SQLException("역할을 찾을 수 없습니다.")

            // 플레이어의 역할 업데이트
            UserKeyCards.update({ UserKeyCards.id eq playerEntity.id }) {
                it[role] = EntityID(roleId, Roles)
            }
        }
    }

    var role by UserKeyCards.role
}