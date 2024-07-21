package io.github.hxxniverse.hobeaktown.feature.user

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoney
import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoneys
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Role
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.Roles
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

enum class Job {
    CITIZEN, POLICE, EMPLOYEE, BANKER, NATIONAL, SOLDIER, TRAINEE, SELLER, VIP
}

object Users : UUIDTable() {
    val name = varchar("name", 255).default("")
    val age = integer("age").default(20)
    val specialNote = varchar("special_note", 100).default("")
    val penaltyPoints = integer("penalty_points").default(0)
    val job = enumerationByName("job", 20, Job::class).default(Job.CITIZEN)
    val money = reference("money", UserMoneys)
    val role = reference("role", Roles)
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users) {
        val admin = User.new(UUID.fromString("00000000-0000-0000-0000-000000000000")) {}
        val auction = User.new(UUID.fromString("00000000-0000-0000-0000-000000000001")) {}
    }

    var name by Users.name
    var age by Users.age
    var specialNote by Users.specialNote
    var penaltyPoints by Users.penaltyPoints
    var job by Users.job
    var money by UserMoney referencedOn Users.money
    var role by Role referencedOn  Roles.role

    /** Get the player instance from the UUID If player is't online return null */
    val player: Player? get() = Bukkit.getPlayer(id.value)

    /** Get the offline player instance from the UUID */
    val offlinePlayer: OfflinePlayer get() = Bukkit.getOfflinePlayer(id.value)
}
