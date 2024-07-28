package io.github.hxxniverse.hobeaktown.feature.fish.ui

import io.github.hxxniverse.hobeaktown.feature.fish.entity.Fish
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

abstract class FishSetUi(title: String, private val tier: Int) : CustomInventory(title, 54) {
    protected var page = 0
    protected lateinit var fishes: List<ItemStack>

    init {
        background(BACKGROUND)
        setupNavigationButtons()
        setupFishTierButtons()
        loadFishes()
        display()
    }

    private fun setupNavigationButtons() {
        button(5 to 1, icon {
            type = Material.GREEN_STAINED_GLASS_PANE
            name = component("이전 페이지")
        }) {
            if (page > 0) {
                page--
                display()
            }
        }

        button(5 to 9, icon {
            type = Material.GREEN_STAINED_GLASS_PANE
            name = component("다음 페이지")
        }) {
            if ((page + 1) * 28 < fishes.size) {
                page++
                display()
            }
        }
    }

    private fun setupFishTierButtons() {
        button(1 to 2, icon {
            type = Material.RED_STAINED_GLASS_PANE
            name = component("1등급 물고기 조회")
        }) {
            player.closeInventory()
            FishFirstSetUi().open(player)
        }

        button(1 to 3, icon {
            type = Material.ORANGE_STAINED_GLASS_PANE
            name = component("2등급 물고기 조회")
        }) {
            player.closeInventory()
            FishSecondSetUi().open(player)
        }

        button(1 to 4, icon {
            type = Material.YELLOW_STAINED_GLASS_PANE
            name = component("3등급 물고기 조회")
        }) {
            player.closeInventory()
            FishThirdSetUi().open(player)
        }

        button(1 to 5, icon {
            type = Material.PURPLE_STAINED_GLASS_PANE
            name = component("레전더리 물고기 조회")
        }) {
            player.closeInventory()
            FishLegendSetUi().open(player)
        }
    }

    private fun loadFishes() {
        fishes = Fish.getFishes(tier).map { it.item }
    }

    protected fun display() {
        empty(2 to 2, 5 to 8)
        val startIndex = page * 28
        val endIndex = (startIndex + 28).coerceAtMost(fishes.size)

        var slotIndex = 0
        for (i in startIndex until endIndex) {
            val row = 2 + slotIndex / 7
            val col = 2 + slotIndex % 7

            item(row to col, fishes[i]) {
                if (it.click.isShiftClick && it.click.isRightClick) {
                    player.inventory.addItem(fishes[i])
                    Fish.removeFish(fishes[i])
                    fishes = Fish.getFishes(tier).map { it.item }
                    display()
                } else if (it.click.isLeftClick) {
                    player.inventory.addItem(fishes[i])
                }
            }
            slotIndex++
        }
    }
}

class FishFirstSetUi : FishSetUi("1등급 설정 (좌클=지급/쉬프트+우클=삭제)", 1)

class FishSecondSetUi : FishSetUi("2등급 설정 (좌클=지급/쉬프트+우클=삭제)", 2)

class FishThirdSetUi : FishSetUi("3등급 설정 (좌클=지급/쉬프트+우클=삭제", 3)

class FishLegendSetUi : FishSetUi("레전더리 설정 (좌클=지급/쉬프트+우클=삭제)", 10)