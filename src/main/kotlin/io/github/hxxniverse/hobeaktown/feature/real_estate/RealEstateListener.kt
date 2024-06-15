package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.feature.real_estate.ui.RealEstateBuyUi
import io.github.hxxniverse.hobeaktown.util.coroutine.Hobeak
import io.github.hxxniverse.hobeaktown.util.extension.setPersistentData
import io.github.hxxniverse.hobeaktown.util.extension.text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration

class RealEstateListener : Listener {
    @EventHandler
    fun onRealEstateTerritorialEstablishment(event: PlayerInteractEvent) {
        val player = event.player
        val itemStack = event.item ?: return

        val realEstateSelection = itemStack.getItemStackRealEstateSelection() ?: return
        val location = event.clickedBlock?.location ?: return

        // 좌클릭
        if (event.action.isLeftClick) {
            event.isCancelled = true
            if (!event.player.isSneaking) {
                realEstateSelection.copy(pos1 = location).toItemStack()
                    .also { player.inventory.setItemInMainHand(it) }
                player.sendMessage("부동산 선택지의 첫번째 지점을 설정하셨습니다.")
            } else {
                realEstateSelection.copy(pos2 = location).toItemStack()
                    .also { player.inventory.setItemInMainHand(it) }
                player.sendMessage("부동산 선택지의 두번째 지점을 설정하셨습니다.")
            }
            return
        }
    }

    @EventHandler
    fun onRealEstateSelectionRightClick(event: PlayerInteractEvent) = transaction {
        val player = event.player
        val block = event.clickedBlock ?: return@transaction

        val realEstate = RealEstate.find { RealEstates.signLocation eq block.location }.firstOrNull() ?: return@transaction

        event.isCancelled = true

        println(realEstate)

        if (player.uniqueId != realEstate.owner) {
            realEstate.showInfo(player)
            return@transaction
        }
    }

    @EventHandler
    fun onRealEstateSelectionCreate(event: SignChangeEvent) {
        val player = event.player
        val itemStack = player.inventory.itemInMainHand

        if (itemStack.getItemStackRealEstateSelection() != null) {
            val realEstateSelection = itemStack.getItemStackRealEstateSelection()!!

//            event.line(0, text(realEstateSelection.name))
//            event.line(1, text("금액 : ${realEstateSelection.price}원"))
//            event.line(2, text("기간 : ${realEstateSelection.due}일"))
//            event.line(3, text("판매중"))

            transaction {
                RealEstate.create(
                    realEstateSelection.name,
                    realEstateSelection.price,
                    realEstateSelection.due,
                    realEstateSelection.type,
                    realEstateSelection.pos1,
                    realEstateSelection.pos2,
                    event.block.location
                ).also { realEstate ->
                    realEstate.updateSign()
                }
            }

            player.inventory.setItemInMainHand(null)
            player.sendMessage("부동산 선택지를 설치하셨습니다.")
            return
        }
    }

    @EventHandler
    fun onPlayerRealEstateBuyEvent(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock ?: return

        if (block.type != Material.OAK_SIGN) return

        val realEstate = transaction {
            RealEstate.find { RealEstates.signLocation eq block.location }.firstOrNull()
        } ?: return

        if (player.uniqueId != realEstate.owner) {
            realEstate.showInfo(player)
            return
        }

        RealEstateBuyUi(realEstate).open(player)
    }

    @EventHandler
    fun onPlayerUseLandAppraisalCertificateEvent(event: PlayerInteractEvent) {
        val player = event.player
        val itemStack = event.item ?: return

        if (itemStack.isSimilar(RealEstatesItem.LAND_APPRAISAL_CERTIFICATE)) {
            event.isCancelled = true
            val block = event.clickedBlock ?: return
            val realEstate = transaction {
                RealEstate.find { RealEstates.signLocation eq block.location }.firstOrNull()
            } ?: return

            // animation C to R like slot machine
            CoroutineScope(Dispatchers.Hobeak).launch {
                // 1초에 5번씩 랜덤으로 등급을 바꿔줌
                repeat(5) {
                    realEstate.grade = RealEstateGrade.entries.random()
                    player.showTitle(
                        Title.title(
                            text(realEstate.grade?.name ?: ""),
                            text(""),
                            Title.Times.times(Duration.ZERO, Duration.ofMillis(200), Duration.ZERO)
                        )
                    )
                    kotlinx.coroutines.delay(200)
                }
                Title.title(
                    text(realEstate.grade?.name ?: ""),
                    text("토지 등급이 결정되었습니다."),
                    Title.Times.times(Duration.ZERO, Duration.ofMillis(200), Duration.ZERO)
                )
            }
        }
    }

    @EventHandler
    fun onInteractRealEstateWorldEvent(event: PlayerInteractEvent) {
        val player = event.player

        if (!isAvailable(player, event.clickedBlock?.location ?: player.location)) {
            event.isCancelled = true
            return
        }

        // 권한이 없다면 이벤트 취소
    }

    @EventHandler
    fun onBlockBreakOnRealEstateWorldEvent(event: BlockBreakEvent) {
        val player = event.player

        if (!isAvailable(player, event.block.location)) {
            event.isCancelled = true
            return
        }

        // 권한이 없다면 이벤트 취소
    }

    @EventHandler
    fun onBlockPlaceOnRealEstateWorldEvent(event: BlockBreakEvent) {
        val player = event.player

        if (!isAvailable(player, event.block.location)) {
            event.isCancelled = true
            return
        }

        // 권한이 없다면 이벤트 취소
    }

    private fun isAvailable(player: Player, target: Location): Boolean {
        // 오피면 허용
        if (player.isOp) return true

        // 월드가 부동산 월드가 아니라면 허용
        if (!RealEstateConfig.configData.realEstateWorld.contains(player.world.name)) return true

        // 내가 있는 영역이 부동산 영역이고 소유주가 나라면 허용
        transaction {
            RealEstate.find { RealEstates.owner eq player.uniqueId }.forEach {
                if (it.isInside(target)) return@transaction true
            }
        }

        // 그 외에는 허용하지 않음
        return false
    }
}