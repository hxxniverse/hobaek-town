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
import org.jetbrains.exposed.sql.transactions.transaction

class RandomBoxOpenUi(
    private val randomBox: RandomBox
) : CustomInventory("${randomBox.name} 아이템 설정", 45) {

    private var criteriaIndex = 0
    private var spinJob: Job? = null

    init {
        inventory {
            transaction {
                background(ItemStack(Material.GRAY_STAINED_GLASS_PANE))

                val randomBoxItems = RandomBoxItem.find { RandomBoxItems.randomBox eq randomBox.id }.shuffled()

                randomBoxItems.subList(0, 7).forEachIndexed { index, randomBoxItem ->
                    display(2 to index + 2, randomBoxItem.itemStack)
                }

                display(1 to 5, ItemStack(Material.DIAMOND).edit { setDisplayName("여기에서 멈춘 아이템이 지급됩니다.") })

                // 4,5 start button
                button(4 to 5, ItemStack(Material.RED_STAINED_GLASS_PANE).edit { setDisplayName("시작") }) {
                    display(
                        4 to 5,
                        ItemStack(Material.YELLOW_STAINED_GLASS_PANE).edit { setDisplayName("잠시만 기다려주세요!") })
                    // 가운데 2,2 ~ 2,8 사이에 아이템이 좌측에서 우측으로 흐르는 느낌으로 무한 회전하도록 구현 약 10초간
                    spinJob = launch {
                        repeat(30) {
                            randomBoxItems.retrieveValues(criteriaIndex, 7).forEachIndexed { index, randomBoxItem ->
                                display(2 to index + 2, randomBoxItem.itemStack)
                            }
                            criteriaIndex = (criteriaIndex + 1) % randomBoxItems.size
                            delay(100)
                        }
                    }
                    spinJob?.invokeOnCompletion {
                        val resultItem = getItem(2 to 5) ?: return@invokeOnCompletion
                        player.inventory.addItem(resultItem)
                        player.sendMessage("아이템이 지급되었습니다.")
                        button(4 to 5, ItemStack(Material.GREEN_STAINED_GLASS_PANE).edit { setDisplayName("닫기") }) {
                            player.closeInventory()
                        }
                    }
                }
            }
        }

        onInventoryClose {
            spinJob?.cancel()
        }
    }
}