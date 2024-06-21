package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.stringParam
import org.jetbrains.exposed.sql.transactions.transaction
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
object UserKeyCards : IntIdTable() {
    val uuid = varchar("uuid", 255).uniqueIndex()
    val role = reference("role_id", Roles)
}

class UserKeyCard(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserKeyCard>(UserKeyCards) {
        @Throws(SQLException::class)
        fun isExists(player: UUID): Boolean = transaction {
            return@transaction UserKeyCard.find { UserKeyCards.uuid eq player.toString() }.firstOrNull() != null;
        }
        @Throws(SQLException::class)
        fun updateMemberRole(player: UUID, roleName: String) = transaction {
            val playerEntity = UserKeyCard.find { UserKeyCards.uuid eq player.toString() }.firstOrNull()
                ?: throw SQLException("플레이어를 찾을 수 없습니다.")

            val roleId = Role.find { Roles.role eq roleName }
                .singleOrNull()?.id?.value
                ?: throw SQLException("역할을 찾을 수 없습니다.")

            // 플레이어의 역할 업데이트
            UserKeyCards.update({ UserKeyCards.uuid eq playerEntity.uuid }) {
                it[role] = EntityID(roleId, Roles)
            }
        }
    }

    var uuid by UserKeyCards.uuid
    var role by UserKeyCards.role
}