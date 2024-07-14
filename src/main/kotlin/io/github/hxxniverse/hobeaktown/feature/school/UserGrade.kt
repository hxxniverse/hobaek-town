package io.github.hxxniverse.hobeaktown.feature.school

import io.github.hxxniverse.hobeaktown.feature.fatigue.config.UserFatigueConfig
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.UserFatigue
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object UserGrades : UUIDTable() {
    val curGrade = integer("cur_grade").default(0)
    val maxGrade = integer("max_grade").default(10)
}

class UserGrade(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<UserGrade>(UserGrades) {
        fun findOrCreate(id: UUID): UserGrade {
            return UserGrade.findById(id) ?: UserGrade.new(id) {
                curGrade = 0;
                maxGrade = 10;
            }
        }
    }
    var curGrade by UserGrades.curGrade
    var maxGrade by UserGrades.maxGrade
}