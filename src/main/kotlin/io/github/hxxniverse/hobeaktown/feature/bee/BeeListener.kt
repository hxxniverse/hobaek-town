package io.github.hxxniverse.hobeaktown.feature.bee

import io.github.hxxniverse.hobeaktown.feature.bee.ui.BeehiveRewardUi
import io.github.hxxniverse.hobeaktown.feature.bee.ui.BeehiveUi
import io.github.hxxniverse.hobeaktown.feature.bee.ui.BeehiveWaitingUi
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

/**
 * < 구현 예정 목록 >
 *     1. BlockPlaceEvent -> 새로운 벌통 생성
 *     2. BlockBreakEvent -> 벌통 파괴
 *     3. PlayerInteractEvent -> 벌통 상호작용
 */

class BeeListener : Listener {
    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if(e.block.type != Material.BEEHIVE) {
            return
        }

        val name: String = e.itemInHand.itemMeta.displayName

        if(name.contains("벌통 생성 블럭: ")) {
            val code: String = name.split("벌통 생성 블럭: ")[1]
            BeehiveSetup.getSetupByCode(code) ?: return
            Beehive.createBeehive(code, e.block.location, null, BeehiveSetup.getItemsByCode(code))

            e.player.sendMessage("§6[양봉]§7 양봉이 가능한 벌통을 놓았습니다. $code")
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if(e.block.type != Material.BEEHIVE) {
            return
        }

        Beehive.getByLocation(e.block.location) ?: return
        Beehive.deleteBeehive(e.block.location)

        e.player.sendMessage("§6[양봉]§7 양봉이 가능한 벌통을 부셨습니다.")
    }

    @EventHandler
    fun onInteractBlock(e: PlayerInteractEvent) {
        if(e.action != Action.RIGHT_CLICK_BLOCK || e.hand != EquipmentSlot.HAND ||  e.clickedBlock == null) {
            return
        }

        if(e.clickedBlock!!.type == Material.BEEHIVE) {
            val bee = Beehive.getByLocation(e.clickedBlock!!.location)

            if(bee == null) {
//                e.player.sendMessage("벌통 감지되지 않음")
            } else {
                if(bee.started) {
                    if(bee.ownerUUID!! != e.player.uniqueId) {
                        e.player.sendMessage("§6[양봉]§7 이 벌통은 다른 사람이 사용중에 있습니다!")
                        return
                    }

                    if(bee.isFinish()) {
                        BeehiveRewardUi.getOrCreate(bee).open(e.player)
                    }
                    else {
                        BeehiveWaitingUi(bee).open(e.player)
                    }
                } else {
                    BeehiveUi(bee).open(e.player)
                }

            }
        }
    }
}
