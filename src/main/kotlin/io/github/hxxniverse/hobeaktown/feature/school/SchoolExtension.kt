package io.github.hxxniverse.hobeaktown.feature.school

import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

var Player.curGrade: Int
    set(value) {
        transaction {
            UserGrade.findOrCreate(uniqueId).curGrade = value
        }
    }
    get() {
        return transaction {
            UserGrade.findOrCreate(uniqueId).curGrade
        }
    }

var Player.maxGrade: Int
    get() {
        return transaction {
            UserGrade.findOrCreate(uniqueId).maxGrade
        }
    }
    set(value) {
        transaction {
            UserGrade.findOrCreate(uniqueId).maxGrade = value
        }
    }
