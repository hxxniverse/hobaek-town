package io.github.hxxniverse.hobeaktown.feature.fish.ui

import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory

class FishSetUi : CustomInventory("물고기 설정", 54) {
    init {
        background(BACKGROUND)

        empty(2 to 2, 8 to 5)
    }
}