package io.github.hxxniverse.hobeaktown.feature.school

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

    }
    var curGrade by UserGrades.curGrade
    var maxGrade by UserGrades.maxGrade
}