package io.github.hxxniverse.hobeaktown.feature.randombox

import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBox
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxItem
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxItems
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.launch
import io.github.hxxniverse.hobeaktown.util.extension.retrieveValues
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class RandomBoxOpenUi(
    private val randomBox: RandomBox
) : CustomInventory("", 54) {

    private var criteriaIndex = 0
    private var spinJob: Job? = null

    init {
        inventory {
            loggedTransaction {
                background(BACKGROUND)

                val randomBoxItems = RandomBoxItem.find { RandomBoxItems.randomBox eq randomBox.id }
                    .map { item -> (0..(item.chance.chance * 100).toInt()).map { item.itemStack }.toList() }.flatten()
                    .shuffled()

                randomBoxItems.subList(0, 7).forEachIndexed { index, randomBoxItem ->
                    display(3 to index + 2, randomBoxItem)
                }

                display(2 to 5, ItemStack(Material.DIAMOND).edit { setDisplayName("여기에서 멈춘 아이템이 지급됩니다.") })

                // 4,5 start button
                button(5 to 4, 5 to 6, ItemStack(Material.RED_STAINED_GLASS_PANE).edit { setDisplayName("시작") }) {
                    player.inventory.itemInMainHand.amount -= 1
                    display(
                        5 to 4, 5 to 6,
                        ItemStack(Material.YELLOW_STAINED_GLASS_PANE).edit { setDisplayName("잠시만 기다려주세요!") })
                    // 가운데 2,2 ~ 2,8 사이에 아이템이 좌측에서 우측으로 흐르는 느낌으로 무한 회전하도록 구현 약 10초간
                    spinJob = launch {
                        repeat(50) {
                            randomBoxItems.retrieveValues(criteriaIndex, 7).forEachIndexed { index, randomBoxItem ->
                                display(3 to index + 2, randomBoxItem)
                            }
                            criteriaIndex = (criteriaIndex + 1) % randomBoxItems.size
                            delay(50)
                        }
                    }
                    spinJob?.invokeOnCompletion {

                        (2..4).forEach { x ->
                            (2..8).filter { it != 5 }.forEach { y ->
                                display(x to y, BACKGROUND)
                            }
                        }

                        button(
                            5 to 4,
                            5 to 6,
                            ItemStack(Material.GREEN_STAINED_GLASS_PANE).edit { setDisplayName("수령하기") }) {
                            val resultItem = getItem(3 to 5) ?: return@button
                            player.inventory.addItem(resultItem)
                            player.sendMessage("아이템이 지급되었습니다.")
                            player.closeInventory()
                        }
                    }
                }
            }
        }

        onInventoryClose {
            spinJob?.cancel()
        }

        onPlayerInventoryClick { event ->
            event.isCancelled = true
        }
    }
}