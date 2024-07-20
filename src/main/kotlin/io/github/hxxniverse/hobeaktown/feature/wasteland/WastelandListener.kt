package io.github.hxxniverse.hobeaktown.feature.wasteland

import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.Wasteland
import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.WastelandSetup
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

/**
 * <구현 예정 목록>
 *     1. onBlockPlace -> 모래, 자갈 놓을 때 Wasteland 등록
 */
class WastelandListener : Listener {
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val block = event.block
        val item = event.itemInHand

        // 자갈이나 모래 블록을 놓을 때만 처리
        if (block.type == Material.GRAVEL || block.type == Material.SAND) {
            val name = item.itemMeta.displayName

            if (name.contains("황무지 설정 블럭 - ")) {
                val code = name.split("황무지 설정 블럭 - ")[1]
                val setup = WastelandSetup.getSetupByCode(code)

                if(setup != null) {
                    Wasteland.addWasteland(code, block.location, block.type)
                    player.sendMessage("황무지가 성공적으로 등록되었습니다: $code")
                }
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        // 자갈이나 모래 블록을 파괴할 때만 처리
        if (block.type == Material.GRAVEL || block.type == Material.SAND) {
            val location = block.location
            val wasteland = Wasteland.getByLocation(location)

            if(wasteland != null) {
                Wasteland.deleteWasteland(location)
                player.sendMessage("황무지가 성공적으로 제거되었습니다: ${wasteland.code}")
            }
        }
    }
}