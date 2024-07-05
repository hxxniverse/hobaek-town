package io.github.hxxniverse.hobeaktown.feature.user

import io.github.hxxniverse.hobeaktown.feature.economy.entity.UserMoney
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.jetbrains.exposed.sql.transactions.transaction

class UserListener : Listener {
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) = transaction {
        val user = User.findById(event.player.uniqueId) ?: User.new(event.player.uniqueId) {
            name = event.player.name
        }.also {
            println("${event.player.name} 유저 정보가 존재하지 않아 새로 생성되었습니다.")
        }

        // money
        UserMoney.findById(user.id.value) ?: UserMoney.new(user.id.value) {
            money = 0
        }.also {
            println("${event.player.name} 유저의 돈 정보가 존재하지 않아 새로 생성되었습니다.")
        }
    }
}