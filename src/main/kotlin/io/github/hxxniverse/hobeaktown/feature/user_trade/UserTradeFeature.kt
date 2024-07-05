package io.github.hxxniverse.hobeaktown.feature.user_trade

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.itemStack
import io.github.hxxniverse.hobeaktown.util.skullMeta
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class UserTradeFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        UserTradeCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

class UserTradeCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("usertrade") {
                then("request") {
                    then("target" to player()) {
                        executes {
                            val target: Player by it
                            player.sendMessage("플레이어 ${target.name}에게 거래 요청을 보냈습니다.")
                            UserTradeManager.request(player, target)
                        }
                    }
                }
                then("accept") {
                    executes {
                        val target = UserTradeManager.getTrade(player)
                        if (target != null) {
                            UserTradeManager.accept(player)
                            UserTradeUi(player, target).open(player)
                            UserTradeUi(player, target).open(target)
                        }
                    }
                }
                then("cancel") {
                    executes {
                        val target = UserTradeManager.getTrade(player)
                        if (target != null) {
                            target.sendMessage("상대방이 거래를 취소했습니다.")
                            UserTradeManager.cancel(player)
                        }
                    }
                }
            }
        }
    }
}

object UserTradeManager {
    private val trades = mutableMapOf<UUID, UUID>()

    fun request(player1: Player, player2: Player) {
        trades[player1.uniqueId] = player2.uniqueId
        trades[player2.uniqueId] = player1.uniqueId
    }

    fun getTrade(player: Player): Player? {
        return trades[player.uniqueId]?.let { Bukkit.getPlayer(it) }
    }

    fun cancel(player: Player) {
        trades.remove(player.uniqueId)
        trades.remove(getTrade(player)?.uniqueId)
    }

    fun accept(player: Player) {
        trades.remove(player.uniqueId)
        trades.remove(getTrade(player)?.uniqueId)
    }
}

class UserTradeUi(
    private val player1: Player,
    private val player2: Player
) : CustomInventory("거래", 54) {

    private var isPlayersJoined = false
    private var player1TradeReady = false
    private var player2TradeReady = false

    init {
        inventory {
            background(BACKGROUND)

            isPlayersJoined = viewer().size == 2

            // 거래 요청자의 플레이어 머리 1,2 2,3
            display(1 to 2, 2 to 3, icon = itemStack {
                type = Material.PLAYER_HEAD
                displayName = "거래 요청자".component()
                skullMeta = skullMeta {
                    playerProfile = player.playerProfile
                }
            })

            // 거래 player2 요청자의 머리 1,7 to 2,8
            display(1 to 7, 2 to 8, icon = itemStack {
                type = Material.PLAYER_HEAD
                displayName = "거래 player2".component()
                skullMeta = skullMeta {
                    playerProfile = player2.playerProfile
                }
            })

            // 거래 요청자의 상태
            display(3 to 1, 3 to 4, icon = itemStack {
                type = Material.RED_STAINED_GLASS_PANE
                displayName = "거래 대기중".component()
            })

            // 거래 player2의 상태
            display(3 to 6, 3 to 9, icon = itemStack {
                type = Material.RED_STAINED_GLASS_PANE
                displayName = "거래 대기중".component()
            })

            // player1 거래 공간
            button(4 to 1, 5 to 4, ItemStack(Material.AIR)) {
                if (player1.uniqueId == player.uniqueId) {
                    it.isCancelled = true
                    return@button
                }

                // 비어 있으면 그냥 아이템 추가
                if (it.currentItem == null || it.currentItem?.type == Material.AIR) {
                    it.cursor.let { it1 -> it.currentItem = it1 }
                    it.setCursor(ItemStack(Material.AIR))
                } else {
                    // 아이템이 있으면 교환
                    val temp = it.currentItem
                    it.currentItem = it.cursor
                    it.setCursor(ItemStack(Material.AIR))
                }
            }

            // player2 거래 공간
            button(4 to 6, 5 to 9, ItemStack(Material.AIR)) {
                if (player2.uniqueId == player.uniqueId) {
                    it.isCancelled = true
                    return@button
                }

                // 비어 있으면 그냥 아이템 추가
                if (it.currentItem == null || it.currentItem?.type == Material.AIR) {
                    it.cursor.let { it1 -> it.currentItem = it1 }
                    it.setCursor(ItemStack(Material.AIR))
                } else {
                    // 아이템이 있으면 교환
                    val temp = it.currentItem
                    it.currentItem = it.cursor
                    it.setCursor(ItemStack(Material.AIR))
                }
            }

            // player1 거절 버튼 및 수락 버튼 6 to 1/6 to 2, 6 to 3/6 to 4
            button(6 to 1, 6 to 2, itemStack = itemStack {
                type = Material.RED_STAINED_GLASS_PANE
                displayName = "거절".component()
            }) {
                player2.closeInventory()
                player1.closeInventory()
            }

            button(6 to 3, 6 to 4, itemStack = itemStack {
                type = Material.GREEN_STAINED_GLASS_PANE
                displayName = "수락".component()
            }) {
                player1TradeReady = true

                display(3 to 1, 3 to 4, icon = itemStack {
                    type = Material.GREEN_STAINED_GLASS_PANE
                    displayName = "준비 완료".component()
                })
            }

            // player2 거절 버튼 및 수락 버튼 6 to 6/6 to 7, 6 to 8/6 to 9
            button(6 to 6, 6 to 7, itemStack = itemStack {
                type = Material.RED_STAINED_GLASS_PANE
                displayName = "거절".component()
            }) {
                player1.closeInventory()
                player2.closeInventory()
            }

            button(6 to 8, 6 to 9, itemStack = itemStack {
                type = Material.GREEN_STAINED_GLASS_PANE
                displayName = "수락".component()
            }) {
                player2TradeReady = true

                display(3 to 6, 3 to 9, icon = itemStack {
                    type = Material.GREEN_STAINED_GLASS_PANE
                    displayName = "준비 완료".component()
                })
            }
        }
    }
}