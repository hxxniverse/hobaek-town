package io.github.hxxniverse.hobeaktown.feature.keycard.connection

import io.github.hxxniverse.hobeaktown.feature.keycard.entity.*
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.sql.SQLException
import java.util.*

class DatabaseManager(private val plugin: JavaPlugin) {
    private var connection: Connection? = null

    @Throws(SQLException::class)
    fun initialize() {
        insertDefaultRoles()
    }

    fun insertPlayer(playerId: UUID) = transaction {
        UserKeyCard.new(playerId) {
            role = (Role.find { Roles.name eq "시민" }.firstOrNull() ?: Role.new { name = "시민" }).id
        }
    }

    @Throws(SQLException::class)
    private fun insertDefaultRoles() = transaction {
        val defaultRoles = arrayOf("시민", "경찰", "시청직원", "회사원", "은행원", "국회의원", "군인", "훈련병", "사업가", "vip")

        defaultRoles.forEach { role ->
            Role.new {
                name = role
            }
        }
    }

    @Throws(SQLException::class)
    fun isExistsRole(role: String): Boolean = transaction {
        return@transaction Role.find { Roles.name eq role }.firstOrNull() != null
    }

    @Throws(SQLException::class)
    fun isExistsKeyCard(name: String, roleName: String): Boolean = transaction {
        // 직업 아이디 가져오기
        val role = Role.find { Roles.name eq roleName }.firstOrNull() ?: return@transaction false
        // 키카드 존재 여부 확인
        val isExists = KeyCard.find { (KeyCards.name eq name) and (KeyCards.role eq role.id) }.firstOrNull() != null

        return@transaction isExists
    }

    @Throws(SQLException::class)
    fun insertRole(role: String) = transaction {
        Role.new {
            name = role
        }
    }

    @Throws(SQLException::class)
    fun insertKeyCard(name: String?, roleName: String?) {
        val getRoleIdQuery = "SELECT id FROM role WHERE name = ?"
        val insertKeyCard = "INSERT INTO keycard (name, role_id) SELECT ?, ? WHERE NOT EXISTS (" +
                "SELECT 1 FROM keycard " +
                "WHERE name = ?" +
                "AND role_id = ?" +
                ");"
        connect().use { connection ->
            connection!!.prepareStatement(insertKeyCard).use { insertKeyCardStmt ->
                connection.prepareStatement(getRoleIdQuery).use { getRoleIdStmt ->
                    getRoleIdStmt.setString(1, roleName)
                    try {
                        getRoleIdStmt.executeQuery().use { rsRole ->
                            if (rsRole.next()) {
                                val roleId = rsRole.getInt("id")
                                insertKeyCardStmt.setString(1, name)
                                insertKeyCardStmt.setInt(2, roleId)
                                insertKeyCardStmt.setString(3, name)
                                insertKeyCardStmt.setInt(4, roleId)
                                insertKeyCardStmt.executeUpdate()
                            } else throw SQLException()
                        }
                    } catch (e: SQLException) {
                        throw SQLException("등록되어 있지 않은 역할입니다.")
                    }
                }
            }
        }
    }

    @Throws(SQLException::class)
    fun updateMemberRole(playerName: String?, roleName: String?) {
        val getPlayerUUIDQuery = "SELECT uuid FROM member WHERE name = ?"
        val getRoleIdQuery = "SELECT id FROM role WHERE name = ?"
        val updateRoleQuery = "UPDATE member SET role_id = ? WHERE uuid = ?"

        try {
            connect().use { connection ->
                connection!!.prepareStatement(getPlayerUUIDQuery).use { getPlayerUUIDStmt ->
                    connection.prepareStatement(getRoleIdQuery).use { getRoleIdStmt ->
                        connection.prepareStatement(updateRoleQuery).use { updateRoleStmt ->
                            getPlayerUUIDStmt.setString(1, playerName)
                            getPlayerUUIDStmt.executeQuery().use { rsUUID ->
                                if (rsUUID.next()) {
                                    val uuid = rsUUID.getString("uuid")

                                    getRoleIdStmt.setString(1, roleName)
                                    try {
                                        getRoleIdStmt.executeQuery().use { rsRole ->
                                            if (rsRole.next()) {
                                                val roleId = rsRole.getInt("id")

                                                updateRoleStmt.setInt(1, roleId)
                                                updateRoleStmt.setString(2, uuid)
                                                updateRoleStmt.executeUpdate()
                                            }
                                        }
                                    } catch (e: SQLException) {
                                        throw SQLException("등록되어 있지 않은 역할입니다.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            throw SQLException("데이터베이스 연동에 실패했습니다.")
        }
    }

    fun insertDoorData(placedLocation: Location, aboveLocation: Location, permission: String?) {
        val insertDoorQuery = "INSERT INTO doors (name, x, y, z, permission) VALUES (?, ?, ?, ?, ?)"

        try {
            connect().use { connection ->
                connection!!.prepareStatement(insertDoorQuery).use { statement ->
                    statement.setString(1, placedLocation.world.name)
                    statement.setDouble(2, placedLocation.x)
                    statement.setDouble(3, placedLocation.y)
                    statement.setDouble(4, placedLocation.z)
                    statement.setString(5, permission)
                    statement.executeUpdate()

                    statement.setString(1, aboveLocation.world.name)
                    statement.setDouble(2, aboveLocation.x)
                    statement.setDouble(3, aboveLocation.y)
                    statement.setDouble(4, aboveLocation.z)
                    statement.setString(5, permission)
                    statement.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun deleteDoorDate(placedLocation: Location) {
        val deleteDoorQuery = "DELETE FROM doors WHERE name = ? AND x = ? AND z = ?"
        try {
            connect().use { connection ->
                connection!!.prepareStatement(deleteDoorQuery).use { statement ->
                    statement.setString(1, placedLocation.world.name)
                    statement.setDouble(2, placedLocation.x)
                    statement.setDouble(3, placedLocation.z)
                    statement.executeUpdate()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    @Throws(SQLException::class)
    fun hasPermission(permission: String?, roleName: String?): Boolean {
        val getRoleIdQuery = "SELECT id FROM role WHERE name = ?"
        val getDoorRolesQuery = "SELECT role_id FROM keycard WHERE name = ?"

        try {
            connect().use { connection ->
                connection!!.prepareStatement(getRoleIdQuery).use { getRoleIdStmt ->
                    connection.prepareStatement(getDoorRolesQuery).use { getDoorRolesStmt ->
                        getRoleIdStmt.setString(1, roleName)
                        val roleRs = getRoleIdStmt.executeQuery()
                        if (!roleRs.next()) {
                            return false
                        }
                        val roleId = roleRs.getInt("id")

                        getDoorRolesStmt.setString(1, permission)
                        val doorRolesRs = getDoorRolesStmt.executeQuery()
                        while (doorRolesRs.next()) {
                            if (doorRolesRs.getInt("role_id") == roleId) {
                                return true
                            }
                        }
                        return false
                    }
                }
            }
        } catch (e: SQLException) {
            throw SQLException(e.message)
        }
    }


    @Throws(SQLException::class)
    fun disconnect() {
        if (connection != null && !connection!!.isClosed) {
            connection!!.close()
        }
    }
}
