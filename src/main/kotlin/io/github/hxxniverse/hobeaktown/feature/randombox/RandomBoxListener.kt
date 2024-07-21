package io.github.hxxniverse.hobeaktown.feature.randombox

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class RandomBoxListener : Listener {

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) = loggedTransaction {
        val player = event.player
        val item = event.item ?: return@loggedTransaction

        val randomBox = item.getRandomBox() ?: return@loggedTransaction

        if (player.isSneaking) {
            if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
                RandomBoxItemListUi(randomBox).open(player)
            } else if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {
                RandomBoxOpenUi(randomBox).open(player)
            }
        }
    }
}