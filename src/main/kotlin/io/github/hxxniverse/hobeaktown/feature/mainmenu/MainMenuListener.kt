package io.github.hxxniverse.hobeaktown.feature.mainmenu

import io.github.hxxniverse.hobeaktown.feature.mainmenu.entity.Playtime
import io.github.hxxniverse.hobeaktown.feature.mainmenu.ui.MainMenuUi
import org.bukkit.SoundCategory
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class MainMenuListener : Listener {
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        // Init 대용
        Playtime.getTotalPlaytime(e.player.uniqueId)
        Playtime.getDayPlaytime(e.player.uniqueId)
    }

    @EventHandler
    fun onSwap(e: PlayerSwapHandItemsEvent) {
        if(e.player.isSneaking) {
            e.player.playSound(e.player.location, "custom.hobaek1", SoundCategory.MASTER, 1F, 1F)

            MainMenuUi().open(e.player)
        }
    }
}