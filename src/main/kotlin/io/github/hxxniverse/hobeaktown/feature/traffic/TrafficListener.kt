package io.github.hxxniverse.hobeaktown.feature.traffic

import io.github.hxxniverse.hobeaktown.util.coroutine.runTaskLater
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.jetbrains.exposed.sql.transactions.transaction

class TrafficListener : Listener {
    @EventHandler
    fun onSubwayTicketBoxInteractEvent(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        val clickedBlock = event.clickedBlock ?: return

        val ticketBox = transaction {
            SubwayTicketBox.find { SubwayTicketBoxes.location eq clickedBlock.location }.firstOrNull()
        } ?: return

        if (item.isSubwayTicket()) {
            player.sendMessage("You have used a subway ticket to go to ${ticketBox.to.name}")
            ticketBox.to.scenery.let { it?.let { it1 -> player.teleport(it1) } }
        } else {
            player.sendMessage("You need a subway ticket to use this box.")
        }
    }

    @EventHandler
    fun onBusTicketBoxInteractEvent(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        val clickedBlock = event.clickedBlock ?: return

        val ticketBox = transaction {
            BusTicketBox.find { BusTicketBoxes.location eq clickedBlock.location }.firstOrNull()
        } ?: return

        if (item.isBusTicket()) {
            player.sendMessage("You have used a bus ticket to go to ${ticketBox.to.name}")
            ticketBox.to.scenery.let { it?.let { it1 -> player.teleport(it1) } }
            runTaskLater(3000L) {
                player.sendMessage("You have arrived at ${ticketBox.to.name}")
            }
        } else {
            player.sendMessage("You need a bus ticket to use this box.")
        }
    }
}