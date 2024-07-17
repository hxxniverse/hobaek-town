package io.github.hxxniverse.hobeaktown.feature.area

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.pretty
import io.github.hxxniverse.hobeaktown.util.itemStack
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.plugin.java.JavaPlugin
import java.util.*


class AreaFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(AreaListener(), plugin)
        plugin.kommand {
            register("area") {
                then("create") {
                    then("type" to dynamicByEnum(EnumSet.allOf(AreaType::class.java))) {
                        then("name" to string(StringType.GREEDY_PHRASE)) {
                            executes {
                                val type: AreaType by it
                                val name: String by it

                                val (pos1, pos2) = player.let { it.pos1() to it.pos2() }
                                if (pos1 == null || pos2 == null) {
                                    player.sendMessage("§c위치를 설정해주세요.")
                                    return@executes
                                }
                                player.sendMessage("§a첫번째 위치: ${pos1.pretty()}, 두번째 위치: ${pos2.pretty()}")
                                Area.new {
                                    this.name = name
                                    this.pos1 = pos1
                                    this.pos2 = pos2
                                    this.type = type
                                }
                            }
                        }
                    }
                }
                then("selector") {
                    executes {
                        player.inventory.addItem(itemStack {
                            type = Material.NETHER_STAR
                            displayName = "위치 선택기".component()
                            addPersistentData("posSelectItem", "posSelectItem")
                        })
                        player.sendMessage("§a위치 선택기를 지급하였습니다.")
                    }
                }
            }
        }
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

class AreaListener : Listener {

    private val posSelectItem = itemStack {
        type = Material.NETHER_STAR
        displayName = "위치 선택기".component()
        addPersistentData("posSelectItem", "posSelectItem")
    }

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand

        if (event.hand != EquipmentSlot.HAND) return

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
                    event.isCancelled = true
                }

                Action.RIGHT_CLICK_BLOCK -> {
                    PosSelectManager.setPos2(player, event.clickedBlock!!.location)
                    player.sendMessage(
                        "§a두번째 위치를 설정하였습니다. pos1: ${
                            PosSelectManager.getPos1(
                                player
                            )?.pretty() ?: "설정되지 않음"
                        }, pos2: ${
                            PosSelectManager.getPos2(
                                player
                            )?.pretty() ?: "설정되지 않음"
                        }"
                    )
                    event.isCancelled = true
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