package io.github.hxxniverse.hobeaktown.feature.real_estate.ui

import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstate
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import org.bukkit.Material
import org.jetbrains.exposed.sql.transactions.transaction

class RealEstateBuyUi(
    private val realEstate: RealEstate
) : CustomInventory("구매하시겠습니까?", 27) {
    init {
        inventory {
            transaction {
                button(
                    2 to 2,
                    icon {
                        type = Material.GREEN_WOOL
                        name = "구매".component()
                    }
                ) {
                    realEstate.buy(player)
                    realEstate.updateSign()
                    player.closeInventory()
                }

                button(
                    2 to 5,
                    icon {
                        type = Material.PAPER
                        name = "정보".component()
                    }
                )

                button(
                    2 to 8,
                    icon {
                        type = Material.RED_WOOL
                        name = "취소".component()
                    }
                ) {
                    player.closeInventory()
                }
            }
        }
    }
}