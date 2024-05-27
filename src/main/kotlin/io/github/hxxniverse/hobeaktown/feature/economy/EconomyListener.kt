package io.github.hxxniverse.hobeaktown.feature.economy

import io.github.hxxniverse.hobeaktown.feature.economy.ui.AtmMenuUi
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent

class EconomyListener : Listener {
    @EventHandler
    fun onPlayerInteractEntityEvent(event: PlayerInteractEntityEvent) {
        val isAtm = event.rightClicked.getPersistentData<Boolean>("isAtmBlock") ?: return
        if (isAtm) {
            AtmMenuUi().open(event.player)
            event.isCancelled = true
        }
    }
}