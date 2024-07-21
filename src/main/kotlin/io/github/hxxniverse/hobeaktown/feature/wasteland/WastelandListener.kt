package io.github.hxxniverse.hobeaktown.feature.wasteland

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin
import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.Brush
import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.Wasteland
import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.WastelandSetup
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.scheduler.BukkitRunnable

/**
 * <구현 예정 목록>
 *     1. onBlockPlace -> 모래, 자갈 놓을 때 Wasteland 등록
 */
class WastelandListener : Listener {
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        val player = e.player
        val block = e.block
        val item = e.itemInHand

        // 자갈이나 모래 블록을 놓을 때만 처리
        if (block.type == Material.GRAVEL || block.type == Material.SAND) {
            val name = item.itemMeta.displayName

            if (name.contains("황무지 설정 블럭 - ")) {
                val code = name.split("황무지 설정 블럭 - ")[1]
                val setup = WastelandSetup.getSetupByCode(code)

                if(setup != null) {
                    Wasteland.addWasteland(code, block.location, block.type)
                    player.sendMessage("§6[황무지]§7 황무지가 성공적으로 설치되었습니다: $code")
                }
            }
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val player = e.player
        val block = e.block

        // 자갈이나 모래 블록을 파괴할 때만 처리
        if(block.type == Material.GRAVEL || block.type == Material.SAND || block.type == Material.BEDROCK) {
            val location = block.location
            val wasteland = Wasteland.getByLocation(location)

            if(wasteland != null) {
                Wasteland.deleteWasteland(location)
                WastelandFeature.removeWaiting(wasteland.location)

                player.sendMessage("§6[황무지]§7 황무지가 성공적으로 제거되었습니다: ${wasteland.code}")
            }
        }
    }

    @EventHandler
    fun onInteractBlock(e: PlayerInteractEvent) {
        if(e.action != Action.RIGHT_CLICK_BLOCK || e.hand != EquipmentSlot.HAND ||  e.clickedBlock == null) {
            return
        }

        if(e.player.inventory.itemInMainHand.type != Material.BRUSH) {
            return
        }

        if(e.clickedBlock!!.type != Material.SAND && e.clickedBlock!!.type != Material.GRAVEL) {
            return
        }

        val block = e.clickedBlock!!
        val wasteland = Wasteland.getByLocation(block.location) ?: return

        val brush = Brush.getByItemStack(e.player.inventory.itemInMainHand)
        if (brush == null) {
            e.player.sendMessage("§6[황무지]§7 현재 들고있는 솔은 등록되지 않은 솔입니다.")
            return
        }

        val reward = WastelandFeature.randomItem(wasteland.getRewards(), brush.level)

        e.player.sendMessage("§6[황무지]§7 아이템을 찾았습니다.")
        e.player.inventory.addItem(reward)

        WastelandFeature.addWaiting(block.location)
        block.type = Material.BEDROCK

        object : BukkitRunnable() {
            override fun run() {
                WastelandFeature.removeWaiting(block.location)
                block.type = wasteland.material
            }
        }.runTaskLater(HobeakTownPlugin.plugin, 20L * 60) // TEST
    }
}