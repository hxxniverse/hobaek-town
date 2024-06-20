package io.github.hxxniverse.hobeaktown.feature.keycard

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.data.Openable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException

class KeyCardListener: Listener {
    private var lastEventTime: Long = 0
    private val EVENT_COOLDOWN: Long = 500

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) = transaction {
        if(!UserKeyCard.isExists(event.player.uniqueId)){
            val role = Role.find { Roles.role eq "시민" }.firstOrNull() ?: return@transaction;
            UserKeyCard.new {
                this.uuid = event.player.uniqueId.toString()
                this.role = role.id;
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.block.type == Material.IRON_DOOR) {
            val itemMeta: ItemMeta? = event.itemInHand.itemMeta;
            if (itemMeta != null && itemMeta.persistentDataContainer.has(
                    NamespacedKey(plugin, "Name"),
                    PersistentDataType.STRING
                )
            ) {
                val name: String? =
                    itemMeta.persistentDataContainer.get(NamespacedKey(plugin, "Name"), PersistentDataType.STRING)
                if (name != null) {
                    val location = event.block.location
                    KeyCardDoor.insertDoorData(location, name)
                }
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.block.type == Material.IRON_DOOR) {
            val location = event.block.location
            KeyCardDoor.delete(location)
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEventTime < EVENT_COOLDOWN) {
            return
        }
        lastEventTime = currentTime

        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            val block = event.clickedBlock
            if (block != null && block.type == Material.IRON_DOOR) {
                val itemInHand = event.player.inventory.itemInMainHand
                val itemMeta = itemInHand.itemMeta
                if (itemInHand.type != Material.IRON_DOOR) {
                    val name: String =
                        itemMeta.persistentDataContainer.get(
                            NamespacedKey(plugin, "Name"),
                            PersistentDataType.STRING
                        ).toString()
                    try {
                        if (KeyCardDoor.checkName(block.location, name)) {
                            val state = block.state
                            if (state.blockData is Openable) {
                                val openable = state.blockData as Openable
                                openable.isOpen = !openable.isOpen
                                state.blockData = openable
                                state.update()
                                event.player.sendMessage("문이 열렸습니다!")
                            }
                        } else {
                            event.player.sendMessage("키카드가 맞지 않습니다.")
                        }
                    } catch (e: SQLException) {
                        event.player.sendMessage("문을 여는 중 오류가 발생했습니다.")
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
