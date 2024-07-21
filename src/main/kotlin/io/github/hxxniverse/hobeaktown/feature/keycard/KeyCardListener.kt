package io.github.hxxniverse.hobeaktown.feature.keycard

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.*
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.data.Openable
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.sql.SQLException
import java.util.*

class KeyCardListener : Listener {
    private var lastEventTime: Long = 0
    private val EVENT_COOLDOWN: Long = 500

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) = loggedTransaction {
        if (!UserKeyCard.isExists(event.player.uniqueId)) {
            val role = Role.find { Roles.role eq "시민" }.firstOrNull() ?: return@loggedTransaction;
            UserKeyCard.new {
                this.role = role.id;
            }
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.block.type == Material.IRON_DOOR) {
            val itemMeta: ItemMeta = event.itemInHand.itemMeta ?: return;
            if (itemMeta.persistentDataContainer.has(
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
            val itemInHand = event.player.inventory.itemInMainHand

            if (block != null && block.type == Material.IRON_DOOR) {
                if (itemInHand.type == Material.NETHER_STAR) {
                    val itemMeta: ItemMeta = itemInHand.itemMeta ?: return
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
                                plugin.logger.info("문이 열렸습니다!")
                            }
                        } else {
                            plugin.logger.info("키카드가 맞지 않습니다.")
                        }
                    } catch (e: SQLException) {
                        plugin.logger.info("문을 여는 중 오류가 발생했습니다.")
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEntityEvent) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEventTime < EVENT_COOLDOWN) {
            return
        }
        lastEventTime = currentTime

        val itemMeta: ItemMeta = event.player.inventory.itemInMainHand.itemMeta ?: return;
        if (event.player.inventory.itemInMainHand.type == Material.NETHER_STAR) {
            if (event.rightClicked.type == EntityType.PLAYER) {
                val role = itemMeta.persistentDataContainer.get(
                    NamespacedKey(plugin, "Role"),
                    PersistentDataType.STRING
                ).toString()
                if (Role.isExistsRole(role)) {
                    UserKeyCard.updateMemberRole(event.rightClicked.uniqueId, role)
                }
            }
        }
        if (event.player.inventory.itemInMainHand.type == Material.BLAZE_ROD) {
            if (event.rightClicked.type == EntityType.PLAYER) {
                val role = itemMeta.persistentDataContainer.get(
                    NamespacedKey(plugin, "Role"),
                    PersistentDataType.STRING
                ).toString()
                if (Role.isExistsRole(role)) {
                    UserKeyCard.updateMemberRole(event.rightClicked.uniqueId, "시민")
                }
            }
        }
    }
}
