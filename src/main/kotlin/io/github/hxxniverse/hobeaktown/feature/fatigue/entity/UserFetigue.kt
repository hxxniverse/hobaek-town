package io.github.hxxniverse.hobeaktown.feature.fatigue.entity

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoneys.default
import io.github.hxxniverse.hobeaktown.feature.fatigue.Status
import org.jetbrains.exposed.dao.id.UUIDTable

object UserFetigue : UUIDTable() {
    val fetigue = integer("fetigue").default(100)
    val cash = varchar("status", 30).default(Status.Normal.toString())
}