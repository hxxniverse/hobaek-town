package io.github.hxxniverse.hobeaktown.feature.real_estate

import io.github.hxxniverse.hobeaktown.feature.real_estate.ui.RealEstateBuyUi
import io.github.hxxniverse.hobeaktown.feature.real_estate.ui.RealEstateCertificationUi
import io.github.hxxniverse.hobeaktown.util.coroutine.Hobeak
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import io.github.hxxniverse.hobeaktown.util.extension.pretty
import io.github.hxxniverse.hobeaktown.util.extension.text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.title.Title
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import kotlin.math.min

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

        if (event.hand != EquipmentSlot.HAND) {
            return@transaction
        }

        val realEstate =
            RealEstate.find { RealEstates.signLocation eq block.location }.firstOrNull() ?: return@transaction

        event.isCancelled = true
        realEstate.updateSign()
    }

    @EventHandler
    fun onRealEstateSelectionCreate(event: SignChangeEvent) {
        val player = event.player
        val itemStack = player.inventory.itemInMainHand

        if (itemStack.getItemStackRealEstateSelection() != null) {
            val realEstateSelection = itemStack.getItemStackRealEstateSelection()!!

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

        if (realEstate.owner == null) {
            RealEstateBuyUi(realEstate).open(player)
            return
        }

        if (realEstate.owner == player.uniqueId) {
            return
        }
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
            }

            if (realEstate == null) {
                player.sendMessage("해당 지역은 부동산이 아닙니다.")
                return
            }

            // 본인 토지에서만 사용이 가능함
            if (realEstate.owner != player.uniqueId) {
                return
            }

            // animation C to R like slot machine
            CoroutineScope(Dispatchers.Hobeak).launch {
                // 1초에 5번씩 랜덤으로 등급을 바꿔줌
                repeat(5 * 10) {
                    transaction {
                        val grade = RealEstateGrade.entries.random()
                        realEstate.grade = grade
                        realEstate.pos2 = realEstate.pos2.clone().set(
                            realEstate.pos2.x,
                            realEstate.pos1.y + grade.yLimit,
                            realEstate.pos2.z
                        )
                        println("pos1: ${realEstate.pos1.pretty()} pos2: ${realEstate.pos2.pretty()} grade: ${realEstate.grade}")
                    }
                    player.showTitle(
                        Title.title(
                            text(realEstate.grade?.name ?: ""),
                            text("토지 등급을 결정중입니다."),
                            Title.Times.times(Duration.ZERO, Duration.ofMillis(200), Duration.ZERO)
                        )
                    )
                    kotlinx.coroutines.delay(100)
                }
                player.showTitle(
                    Title.title(
                        text(realEstate.grade?.name ?: ""),
                        text("토지 등급이 결정되었습니다."),
                        Title.Times.times(Duration.ZERO, Duration.ofMillis(2000), Duration.ZERO)
                    )
                )
                realEstate.saveScheme()
                realEstate.updateSign()
                println("saveScheme ${realEstate.pos1.pretty()} ~ ${realEstate.pos2.pretty()}")
            }
        }
    }

    @EventHandler
    fun onRightClickRealEstateCertificate(event: PlayerInteractEvent) {
        val player = event.player
        val itemStack = event.item ?: return

        val realEstate = itemStack.getPersistentData<Int>("realEstateId")?.let {
            transaction {
                RealEstate.findById(it)
            }
        } ?: return

        event.isCancelled = true
        RealEstateCertificationUi(realEstate).open(player)
    }

    @EventHandler
    fun onInteractRealEstateWorldEvent(event: PlayerInteractEvent) {
        val player = event.player

        if (!isAvailable(player, event.clickedBlock?.location ?: player.location)) {
            println("권한이 없음")
            event.isCancelled = true
            return
        }

        // 권한이 없다면 이벤트 취소
        println("권한이 있음")
    }

    @EventHandler
    fun onBlockBreakOnRealEstateWorldEvent(event: BlockBreakEvent) {
        val player = event.player

        if (!isAvailable(player, event.block.location)) {
            println("권한이 없음")
            event.isCancelled = true
            return
        }

        // 권한이 없다면 이벤트 취소
        println("권한이 있음")
    }

    @EventHandler
    fun onBlockPlaceOnRealEstateWorldEvent(event: BlockPlaceEvent) {
        val player = event.player

        if (!isAvailable(player, event.block.location)) {
            println("권한이 없음")
            event.isCancelled = true
            return
        }

        // 권한이 없다면 이벤트 취소
        println("권한이 있음")
    }

    private fun isAvailable(player: Player, target: Location): Boolean {
        // 오피면 허용
        if (player.isOp) return true
        println("오피는 아님")

        // 월드가 부동산 월드가 아니라면 허용
        if (!RealEstateConfig.configData.realEstateWorld.contains(player.world.name)) return true
        println("부동산 월드임")

        // 내가 있는 영역이 부동산 영역이고 소유주가 나라면 허용
        return transaction {
            // 보유중인 부동산 목록
            val realEstates = RealEstate.find { RealEstates.owner eq player.uniqueId }.toList()
            val belongsRealEstate =
                RealEstateMember.find { RealEstateMembers.member eq player.uniqueId }.map { it.realEstate }.toList()

            listOf(realEstates, belongsRealEstate).flatten().forEach {
                if (it.isInside(target)) {
                    return@transaction true
                }
            }

            return@transaction false
        }
    }
}