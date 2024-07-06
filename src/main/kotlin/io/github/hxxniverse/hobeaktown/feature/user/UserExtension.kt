package io.github.hxxniverse.hobeaktown.feature.user

import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

val Player.user: User
    get() = transaction {
        User.findById(uniqueId) ?: User.new(uniqueId) {
            name = name
        }
    }