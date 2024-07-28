
import io.github.hxxniverse.hobeaktown.HobeakTownPlugin
import io.github.hxxniverse.hobeaktown.feature.fish.FishGame
import io.github.hxxniverse.hobeaktown.feature.fish.entity.Fish
import io.github.hxxniverse.hobeaktown.feature.fish.entity.FishingRod
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

@Suppress("DEPRECATION")
class FishListener : Listener {
    @EventHandler
    fun onFish(e: PlayerFishEvent) {
        if (e.state != PlayerFishEvent.State.CAUGHT_FISH && e.state != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return
        }

        e.caught?.remove()

        val player = e.player
        var fishingRod: FishingRod? = null
        if(player.inventory.itemInMainHand.type == Material.FISHING_ROD) {
            fishingRod = FishingRod.getByItemStack(player.inventory.itemInMainHand)
        } else if (player.inventory.itemInOffHand.type == Material.FISHING_ROD) {
            fishingRod = FishingRod.getByItemStack(player.inventory.itemInOffHand)
        }

        if (fishingRod == null) {
            player.sendMessage("§9[낚시]§7 현재 사용중인 낚싯대는 등록되지 않은 낚싯대입니다.")
            return
        }

        object : BukkitRunnable() {
            override fun run() {
                val reward = Fish.randomFish(fishingRod.level)
                FishGame.startGame(player, reward)
            }
        }.runTaskLater(HobeakTownPlugin.plugin, 1L)
    }

    @EventHandler
    fun onInventoryOpen(e: InventoryOpenEvent) {
        if (FishGame.isGaming(e.player as Player)) {
            e.isCancelled = true
            e.player.sendMessage("§9[낚시]§7 낚시 중에는 인벤토리를 열 수 없습니다.")
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (FishGame.isGaming(e.whoClicked as Player)) {
            e.whoClicked.sendMessage("§9[낚시]§7 낚시 중에는 인벤토리를 클릭할 수 없습니다.")

            (e.whoClicked as Player).sendTitle("§c실패", "", 4, 20, 4)
            FishGame.stopGame(e.whoClicked as Player)
        }
    }

    @EventHandler
    fun onDrop(e: PlayerDropItemEvent) {
        if(FishGame.isGaming(e.player)) {
            e.isCancelled = true
            e.player.sendMessage("§9[낚시]§7 낚시 중에는 아이템을 버릴 수 없습니다.")
            e.player.sendTitle("§c실패", "", 4, 20, 4)
            FishGame.stopGame(e.player)
        }
    }

    @EventHandler
    fun onSwap(e: PlayerSwapHandItemsEvent) {
        if(FishGame.isGaming(e.player)) {
            e.isCancelled = true
            e.player.sendMessage("§9[낚시]§7 낚시 중에는 손을 교체할 수 없습니다.")
        }
    }

    @EventHandler
    fun onChangeSlot(e: PlayerItemHeldEvent) {
        if(FishGame.isGaming(e.player)) {
            e.isCancelled = true
            e.player.sendMessage("§9[낚시]§7 낚시 중에는 인벤토리 슬롯을 변경하지 마세요.")
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if(FishGame.isGaming(e.player)) {
            FishGame.stopGame(e.player)
        }
    }

    @EventHandler
    fun onDamage(e: EntityDamageEvent) {
        if(e.entity is Player && FishGame.isGaming(e.entity as Player)) {
            val player = e.entity as Player
            player.sendMessage("§9[낚시]§7 낚시 도중 데미지를 받아 낚시가 취소되었습니다.")
            FishGame.stopGame(player)
            player.sendTitle("§c실패", "", 4, 20, 4)
        }
    }

    @EventHandler
    fun onDeath(e: PlayerDeathEvent) {
        if(FishGame.isGaming(e.player)) {
            FishGame.stopGame(e.player)
        }
    }

    private val lastRightClickTicks: MutableMap<UUID, Int> = mutableMapOf()

    @EventHandler
    fun onInteract(e: PlayerInteractEvent) {
        if (!FishGame.isGaming(e.player)) {
            return
        }

        e.isCancelled = true

        val player = e.player
        val currentTick = player.world.fullTime.toInt()

        val input: Int = when (e.action) {
            Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> {
                val lastRightClickTick = lastRightClickTicks[player.uniqueId]
                if (lastRightClickTick != null && currentTick == lastRightClickTick) {
                    return // 같은 틱 내에서 발생한 좌클릭 무시
                }
                0 // 좌클릭으로 처리
            }
            Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> {
                lastRightClickTicks[player.uniqueId] = currentTick
                1 // 우클릭으로 처리
            }
            else -> return
        }

        val game = FishGame.getGame(player) ?: return
        val result = game.input(input)

        when (result) {
            0 -> {
                player.sendTitle("§c실패", "", 4, 20, 4)
                FishGame.stopGame(player)
            }
            2 -> {
                player.sendTitle("§a성공", "", 4, 20, 4)
                FishGame.stopGame(player)
                player.inventory.addItem(game.reward)
            }
        }
    }
}