package io.github.hxxniverse.hobeaktown.feature.real_estate

import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class RealEstateListener : Listener {
    @EventHandler
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        val player = event.player
        val itemStack = event.itemInHand
        val block = event.block

        if (itemStack.getItemStackRealEstateSelection() != null) {
            val sign = block.world.getBlockAt(block.location.add(0.0, 1.0, 0.0)) as Sign
            player.sendMessage("부동산 선택지를 설치하셨습니다.")
        }
    }
}