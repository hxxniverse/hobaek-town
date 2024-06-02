package io.github.hxxniverse.hobeaktown.feature.economy

import io.github.hxxniverse.hobeaktown.feature.economy.entity.Atm
import io.github.hxxniverse.hobeaktown.feature.economy.ui.AtmMenuUi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class EconomyListener : Listener {
    @EventHandler
    fun onPlayerInteractEntityEvent(event: PlayerInteractEvent) {
        val location = event.clickedBlock?.location ?: return
        val isAtm = Atm.findByLocation(location) != null
        if (isAtm) {
            AtmMenuUi().open(event.player)
            event.isCancelled = true
        }
    }
}