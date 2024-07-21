package io.github.hxxniverse.hobeaktown.feature.user

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

val OfflinePlayer.user: User
    get() = loggedTransaction {
        User.findById(uniqueId) ?: User.new(uniqueId) {
            name = this@user.name ?: "Unknown"
        }
    }

val Player.user: User
    get() = loggedTransaction {
        User.findById(uniqueId) ?: User.new(uniqueId) {
            name = this@user.name
        }
    }