package io.github.hxxniverse.hobeaktown.feature.wasteland

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin
import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.Brush
import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.Wasteland
import io.github.hxxniverse.hobeaktown.feature.wasteland.entity.WastelandSetup
import org.bukkit.Material
import org.bukkit.block.BrushableBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
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
        if (block.type == Material.SUSPICIOUS_GRAVEL || block.type == Material.SUSPICIOUS_SAND) {
            val name = item.itemMeta.displayName

            if (name.contains("황무지 설정 블럭 - ")) {
                val code = name.split("황무지 설정 블럭 - ")[1]
                val setup = WastelandSetup.getSetupByCode(code)

                if (setup != null) {
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
        if (block.type == Material.SUSPICIOUS_GRAVEL || block.type == Material.SUSPICIOUS_SAND || block.type == Material.BEDROCK) {
            val location = block.location
            val wasteland = Wasteland.getByLocation(location)

            if (wasteland != null) {
                Wasteland.deleteWasteland(location)
                WastelandFeature.removeWaiting(wasteland.location)

                player.sendMessage("§6[황무지]§7 황무지가 성공적으로 제거되었습니다: ${wasteland.code}")
            }
        }
    }

    @EventHandler
    fun onInteractBlock(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK || e.hand != EquipmentSlot.HAND || e.clickedBlock == null) {
            return
        }

        if (e.player.inventory.itemInMainHand.type != Material.BRUSH) {
            return
        }

        if (e.clickedBlock!!.type != Material.SUSPICIOUS_SAND && e.clickedBlock!!.type != Material.SUSPICIOUS_GRAVEL) {
            return
        }

        if (Wasteland.getByLocation(e.clickedBlock?.location ?: return) == null) {
            return
        }

        val block = e.clickedBlock ?: return
        val state = block.state as BrushableBlock

        if(state.item.type == Material.AIR) {
            state.setItem(ItemStack(Material.NETHER_STAR))
            state.update()
        }
    }

    @EventHandler
    fun onBlockDrop(e: BlockDropItemEvent) {
        if(e.block.type != Material.SUSPICIOUS_SAND && e.block.type != Material.SUSPICIOUS_GRAVEL) {
            return
        }

        e.items.clear()

        val wasteland = Wasteland.getByLocation(e.block.location) ?: return
        val player = e.player
        val brush = Brush.getByItemStack(player.inventory.itemInMainHand)

        if(brush == null) {
            player.sendMessage("§6[황무지]§7 이 솔은 레벨이 등록되어 있지 않은 솔입니다.")
            return
        }

        val reward = WastelandFeature.randomItem(wasteland.getRewards(), brush.level)

        player.inventory.addItem(reward)
        player.sendMessage("§6[황무지]§7 아이템을 찾았습니다.")

        // 1틱 후 블럭 베드락으로 변경
        object : BukkitRunnable() {
            override fun run() {
                WastelandFeature.addWaiting(e.block.location)
                e.block.type = Material.BEDROCK
            }
        }.runTaskLater(HobeakTownPlugin.plugin, 1L)

        // 3분 후 블럭 원상 복귀
        object : BukkitRunnable() {
            override fun run() {
                WastelandFeature.removeWaiting(e.block.location)
                e.block.type = wasteland.material
            }
        }.runTaskLater(HobeakTownPlugin.plugin, 20L * 60 * 3)
    }
}
