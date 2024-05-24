package io.github.hxxniverse.hobeaktown.feature.economy.entity

import io.github.hxxniverse.hobeaktown.feature.economy.EconomyConfig
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object UserMoneys : UUIDTable() {
    val money = integer("money").default(0)
}

class UserMoney(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserMoney>(UserMoneys) {
        fun findOrCreate(id: UUID): UserMoney {
            return findById(id) ?: new(id) {
                money = EconomyConfig.configData.initialMoney
            }
        }
    }

    var money by UserMoneys.money
}