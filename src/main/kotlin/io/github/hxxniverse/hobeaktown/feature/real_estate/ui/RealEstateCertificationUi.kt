package io.github.hxxniverse.hobeaktown.feature.real_estate.ui

import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstate
import io.github.hxxniverse.hobeaktown.feature.real_estate.toItemStack
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Bukkit
import org.bukkit.Material

class RealEstateCertificationUi(
    realEstate: RealEstate
) : CustomInventory("Real Estate Cerficiation", 27) {
    init {
        inventory {
            // 5,2 - 정보
            button(
                2 to 5,
                realEstate.toItemStack()
            )
            // 2,3 - 판매
            button(
                3 to 2,
                icon {
                    type = Material.RED_STAINED_GLASS_PANE
                    name = "판매".component()
                }
            ) {
                RealEstateSellUi(realEstate).open(player)
            }
            // 5,3 - 양도
            button(
                3 to 5,
                icon {
                    type = Material.GREEN_STAINED_GLASS_PANE
                    name = "양도".component()
                }
            ) {
                AnvilInventory(
                    "양도하기",
                    "닉네임을 입력해주세요.",
                    onClickRight = { state ->
                        val nickname = state.text

                        if (nickname.isBlank()) {
                            return@AnvilInventory listOf(AnvilGUI.ResponseAction.replaceInputText("닉네임을 입력해주세요."))
                        }

                        val offlinePlayer = Bukkit.getOfflinePlayerIfCached(nickname)

                        if (offlinePlayer == null) {
                            player.sendMessage("존재하지 않는 플레이어입니다.")
                            return@AnvilInventory listOf(AnvilGUI.ResponseAction.close())
                        }

                        if (!offlinePlayer.hasPlayedBefore()) {
                            player.sendMessage("존재하지 않는 플레이어입니다.")
                            return@AnvilInventory listOf(AnvilGUI.ResponseAction.close())
                        }

                        RealEstateTransferUi(realEstate, offlinePlayer).open(player)
                        listOf(AnvilGUI.ResponseAction.close())
                    }
                ).open(player)
            }
            // 8,3 - 청소
            button(
                3 to 8,
                icon {
                    type = Material.BLUE_STAINED_GLASS_PANE
                    name = "청소".component()
                }
            ) {
                RealEstateCleanUi(realEstate).open(player)
            }
        }
    }
}