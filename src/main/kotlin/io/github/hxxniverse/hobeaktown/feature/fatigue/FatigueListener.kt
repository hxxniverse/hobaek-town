package io.github.hxxniverse.hobeaktown.feature.fatigue;

import com.sk89q.worldedit.util.formatting.text.ComponentBuilders.text
import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.FatigueArea
import io.github.hxxniverse.hobeaktown.feature.fatigue.scheduler.AreaFatigueScheduler
import io.github.hxxniverse.hobeaktown.util.emptyLocation
import io.github.hxxniverse.hobeaktown.util.extension.component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction

class FatigueListener(
    private val feature: FatigueFeature,
    private val scheduler: AreaFatigueScheduler) : Listener {

    @EventHandler
    fun onRealEstateTerritorialEstablishment(event: PlayerInteractEvent) {
        val player = event.player
        val itemStack = event.item ?: return

        var fatigueAreaSelection = itemStack.getItemStackFatigueAreaSelection() ?: return
        val location = event.clickedBlock?.location ?: return

        // 좌클릭
        if (event.action.isLeftClick) {
            event.isCancelled = true
            if (!event.player.isSneaking) {
                fatigueAreaSelection = fatigueAreaSelection.copy(pos1 = location).also {
                    it.toItemStack().also { newItemStack ->
                        player.inventory.setItemInMainHand(newItemStack)
                    }
                }
                player.sendMessage("피로도 구역의 첫번째 지점을 설정하셨습니다.")
            } else {
                fatigueAreaSelection = fatigueAreaSelection.copy(pos2 = location).also {
                    it.toItemStack().also { newItemStack ->
                        player.inventory.setItemInMainHand(newItemStack)
                    }
                }
                player.sendMessage("피로도 구역의 두번째 지점을 설정하셨습니다.")
            }
        }
        if (fatigueAreaSelection.pos1 != emptyLocation() && fatigueAreaSelection.pos2 != emptyLocation()) {
            transaction {
                FatigueArea.create(
                    fatigueAreaSelection.name,
                    fatigueAreaSelection.pos1,
                    fatigueAreaSelection.pos2,
                )
            }
            player.inventory.setItemInMainHand(null)
            player.sendMessage(component("구역이 정상적으로 지정되었습니다.", NamedTextColor.WHITE))
        }
        return
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val from = event.from
        val to = event.to ?: return

        if (from.blockX == to.blockX && from.blockY == to.blockY && from.blockZ == to.blockZ) {
            return
        }
        val currentTime = System.currentTimeMillis()
        feature.walkingTimes[player] = feature.walkingTimes.getOrDefault(player, currentTime)

        val previousZone = feature.playerZones[player]
        val newZone = findAreaForLocation(to)

        if (newZone?.name != previousZone?.name) {
            previousZone?.let {
                player.sendMessage("${it.name} 구역에서 나왔습니다.")
            }
            newZone?.let {
                val fatigueChange = if (it.isMinus) "감소" else "증가"
                player.sendMessage(component("${it.name} 구역에 입장했습니다. 피로도가 ", NamedTextColor.WHITE)
                    .append(component("${it.cycle}", NamedTextColor.BLUE))
                    .append(component("분에 ", NamedTextColor.WHITE))
                    .append(component("${it.fatigue}", NamedTextColor.RED))
                    .append(component("만큼 ", NamedTextColor.WHITE))
                    .append(component(fatigueChange, NamedTextColor.GOLD))
                    .append(component("합니다", NamedTextColor.WHITE)))
            }
            feature.playerZones[player] = newZone
            feature.lastChangeAreaTimes[player] = currentTime;
        }

        val block = player.location.block.getRelative(BlockFace.DOWN)
        if(block.type.name.endsWith("_BED")) {
            player.sendMessage("침대 치료를 시작합니다.")
            feature.playerBedTime[player] = Pair(block.type.name, currentTime);
        } else {
            if(feature.playerBedTime[player] != null) {
                player.sendMessage("침대 치료를 종료합니다.")
                feature.playerBedTime.remove(player)
            }
        }
    }

    private fun findAreaForLocation(location: Location): FatigueArea? {
        return transaction {
            FatigueArea.all().firstOrNull { it.isInside(location) }
        }
    }
}
