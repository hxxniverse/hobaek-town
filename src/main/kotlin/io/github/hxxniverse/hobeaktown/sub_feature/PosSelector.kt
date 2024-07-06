package io.github.hxxniverse.hobeaktown.sub_feature

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.extension.pretty
import io.github.hxxniverse.hobeaktown.util.itemStack
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class PosSelectorFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(PosSelectorListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

class PosSelectorListener : Listener {

    private val posSelectItem = itemStack {
        setType(Material.NETHER_STAR)
        setDisplayName("§e위치 선택기")
        addPersistentData("posSelectItem", "posSelectItem")
    }

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        if (item.isSimilar(posSelectItem)) {
            when (event.action) {
                Action.LEFT_CLICK_BLOCK -> {
                    PosSelectManager.setPos1(player, event.clickedBlock!!.location)
                    player.sendMessage(
                        "§a첫번째 위치를 설정하였습니다. pos1: ${
                            PosSelectManager.getPos1(
                                player
                            )?.pretty() ?: "설정되지 않음"
                        }, pos2: ${
                            PosSelectManager.getPos2(
                                player
                            )?.pretty() ?: "설정되지 않음"
                        }"
                    )
                }

                Action.RIGHT_CLICK_BLOCK -> {
                    PosSelectManager.setPos2(player, event.clickedBlock!!.location)
                    player.sendMessage(
                        "§a첫번째 위치를 설정하였습니다. pos1: ${
                            PosSelectManager.getPos1(
                                player
                            )?.pretty() ?: "설정되지 않음"
                        }, pos2: ${
                            PosSelectManager.getPos2(
                                player
                            )?.pretty() ?: "설정되지 않음"
                        }"
                    )
                }

                else -> {
                    return
                }
            }
        }
    }
}

object PosSelectManager {
    private val pos1 = mutableMapOf<UUID, Location>()
    private val pos2 = mutableMapOf<UUID, Location>()

    fun setPos1(player: Player, location: Location) {
        pos1[player.uniqueId] = location
    }

    fun setPos2(player: Player, location: Location) {
        pos2[player.uniqueId] = location
    }

    fun getPos1(player: Player): Location? {
        return pos1[player.uniqueId]
    }

    fun getPos2(player: Player): Location? {
        return pos2[player.uniqueId]
    }

    fun getPosition(player: Player): Pair<Location?, Location?> {
        return Pair(getPos1(player), getPos2(player))
    }
}

fun Player.pos1(): Location? {
    return PosSelectManager.getPos1(this)
}

fun Player.pos2(): Location? {
    return PosSelectManager.getPos2(this)
}