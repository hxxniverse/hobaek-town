package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import io.github.hxxniverse.hobeaktown.feature.fatigue.config.UserFatigueConfig
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.UserFatigue
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
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
object UserKeyCards : UUIDTable() {
    val role = reference("role_id", Roles)
}

class UserKeyCard(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserKeyCard>(UserKeyCards) {
        fun findOrCreate(id: UUID): UserKeyCard {
            val role = Role.find { Roles.role eq "시민" }.firstOrNull()
                ?: throw IllegalArgumentException("시민 역할이 등록되어있지 않습니다.")
            return UserKeyCard.findById(id) ?: UserKeyCard.new(id) {
                this.role = role.id
            }
        }
        @Throws(SQLException::class)
        fun updateMemberRole(player: UUID, roleName: String) = transaction {
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