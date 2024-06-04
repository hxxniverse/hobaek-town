package io.github.hxxniverse.hobeaktown.feature.real_estate.ui

import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstate
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
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
                    ItemStackBuilder(Material.GREEN_WOOL)
                        .setDisplayName("구매").build(), 2 to 2
                ) {

                }

                button(
                    ItemStackBuilder(Material.PAPER).setDisplayName("[ 정보 ]")
                        .setLore(
                            "토지 이름 : ${realEstate.name}",
                            "기간 : ${realEstate.due}일",
                            "금액 : ${realEstate.price}원"
                        ).build(), 4 to 2
                ) {
                    realEstate.buy(player)
                }

                button(
                    ItemStackBuilder(Material.RED_WOOL)
                        .setDisplayName("취소").build(), 8 to 2
                ) {
                }
            }
        }
    }
}