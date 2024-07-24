package io.github.hxxniverse.hobeaktown.minigame

import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import io.github.hxxniverse.hobeaktown.util.itemStack
import org.bukkit.Material


class HammeringMiniGameUi : CustomInventory("Hammering MiniGame", 54) {

    private var markerPosition: Int = 0
    private var markerMoveSpeed: Long = 20L
    private var successCount: Int = 0
    private val successSlot = listOf(
        4 to 2, 4 to 4, 4 to 6, 5 to 8,
        5 to 3, 5 to 7
    )

    init {
        inventory {
            background(BACKGROUND)

            button(1 to 5, itemStack {
                type = Material.RED_STAINED_GLASS_PANE
                displayName = "기준선".component()
            })

            // 2 to 1 ~ 2 to 9 -> 2 to 1 ~ 2 to 9 순서대로 속도는 랜덤하게 움직인다
            button(2 to (markerPosition + 1), itemStack {
                type = Material.NETHER_STAR
                displayName = "Star".component()
            })

            successSlot.forEachIndexed { index, pair ->
                if (index < successCount) {
                    button(pair, itemStack {
                        type = Material.LIME_STAINED_GLASS_PANE
                        displayName = "Success".component()
                    })
                } else {
                    empty(pair)
                }
            }

            button(5 to 5, itemStack {
                type = Material.IRON_AXE
                displayName = "Hammer".component()
            }) {
                if (markerPosition != 5) {
                    player.closeInventory()
                    return@button
                }

                successCount++
                markerPosition = 0

                if (successCount == successSlot.size) {
                    player.closeInventory()
                    player.sendMessage("성공!")
                }
            }
        }

        onInventoryOpen {
            runTaskRepeat(markerMoveSpeed) {
                if (markerPosition == 9) {
                    markerPosition = 0
                } else {
                    markerPosition++
                }
            }
        }
    }
}
