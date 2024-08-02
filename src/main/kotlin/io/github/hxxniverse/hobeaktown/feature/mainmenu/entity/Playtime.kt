package io.github.hxxniverse.hobeaktown.feature.mainmenu.entity

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object Playtimes : UUIDTable() {
    val dayTime = integer("day_time").default(0)
    val totalTime = integer("total_time").default(0)
}

class Playtime(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Playtime>(Playtimes) {
        fun getDayPlaytime(uuid: UUID): Int {
            return transaction {
                val playtime = findById(uuid) ?: new(uuid) {
                    this.dayTime = 0
                    this.totalTime = 0
                }
                playtime.dayTime
            }
        }

        fun getTotalPlaytime(uuid: UUID): Int {
            return transaction {
                val playtime = findById(uuid) ?: new(uuid) {
                    this.dayTime = 0
                    this.totalTime = 0
                }
                playtime.totalTime
            }
        }

        fun addPlaytime(uuid: UUID, time: Int) {
            transaction {
                val playtime = findById(uuid) ?: new(uuid) {
                    this.dayTime = 0
                    this.totalTime = 0
                }

                playtime.dayTime += time
                playtime.totalTime += time
            }
        }

        fun resetAllDayPlaytime() {
            transaction {
                all().forEach {
                    it.dayTime = 0
                }
            }
        }
    }

    var dayTime by Playtimes.dayTime
    var totalTime by Playtimes.totalTime
}