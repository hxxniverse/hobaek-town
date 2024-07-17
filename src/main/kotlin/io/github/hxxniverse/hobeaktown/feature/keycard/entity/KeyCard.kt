package io.github.hxxniverse.hobeaktown.feature.keycard.entity

import io.github.hxxniverse.hobeaktown.feature.user.Job
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and


object KeyCards : IntIdTable() {
    val name = varchar("name", 50)
    val job = enumeration<Job>("job")
}

class KeyCard(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<KeyCard>(KeyCards) {
        fun isExistsKeyCard(name: String, job: Job): Boolean = loggedTransaction {
            val isExists = KeyCard.find { (KeyCards.name eq name) and (KeyCards.job eq job) }.firstOrNull() != null
            return@loggedTransaction isExists
        }

        fun isExistsKeyName(name: String): Boolean = loggedTransaction {
            val isExists = KeyCard.find { (KeyCards.name eq name) }.firstOrNull() != null
            return@loggedTransaction isExists
        }
    }

    var name by KeyCards.name
    var job by KeyCards.job
}