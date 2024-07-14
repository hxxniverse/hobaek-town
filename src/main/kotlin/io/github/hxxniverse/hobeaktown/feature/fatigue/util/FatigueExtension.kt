package io.github.hxxniverse.hobeaktown.feature.fatigue.util

import io.github.hxxniverse.hobeaktown.feature.fatigue.Status
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.UserFatigue
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

var Player.curFatigue: Int
    set(value) {
        transaction {
            val userFatigue = UserFatigue.findOrCreate(uniqueId)
            userFatigue.curFatigue = value
            userFatigue.status = when {
                userFatigue.curFatigue <= 10 -> Status.Overwork.toString()
                userFatigue.curFatigue <= 20 -> Status.Flu.toString()
                userFatigue.curFatigue <= 40 -> Status.Cold.toString()
                else -> Status.Normal.toString()
            }
        }
    }
    get() {
        return transaction {
            UserFatigue.findOrCreate(uniqueId).curFatigue
        }
    }

var Player.maxFatigue: Int
    set(value) {
        transaction {
            UserFatigue.findOrCreate(uniqueId).maxFatigue = value
        }
    }
    get() {
        return transaction {
            UserFatigue.findOrCreate(uniqueId).maxFatigue
        }
    }
