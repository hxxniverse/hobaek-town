package io.github.hxxniverse.hobeaktown.feature.fatigue.entity

import io.github.hxxniverse.hobeaktown.feature.fatigue.Status
import io.github.hxxniverse.hobeaktown.feature.fatigue.config.UserFatigueConfig
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object UserFatigues : UUIDTable() {
    val curFatigue = integer("cur_fatigue").default(100)
    val maxFatigue = integer("max_fatigue").default(100)
    val status = varchar("status", 30).default(Status.Normal.toString())
}

class UserFatigue(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<UserFatigue>(UserFatigues) {
        fun findOrCreate(id: UUID): UserFatigue {
            return findById(id) ?: new(id) {
                curFatigue = UserFatigueConfig.configData.initialFatigue;
                maxFatigue = UserFatigueConfig.configData.initialMaxFatigue;
                status = UserFatigueConfig.configData.initialStatus.toString();
            }
        }
    }
    var curFatigue by UserFatigues.curFatigue
    var maxFatigue by UserFatigues.maxFatigue
    var status by UserFatigues.status
}