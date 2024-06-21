package io.github.hxxniverse.hobeaktown.feature.real_estate.ui

import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstate
import io.github.hxxniverse.hobeaktown.feature.real_estate.toItemStack
import io.github.hxxniverse.hobeaktown.util.AnvilInventory
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
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
            button(realEstate.toItemStack(), 5 to 2)
            // 2,3 - 판매
            button(ItemStackBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("판매").build(), 2 to 3) {
                RealEstateSellUi(realEstate).open(player)
            }
            // 5,3 - 양도
            button(ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("양도").build(), 5 to 3) {
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
            button(ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("청소").build(), 8 to 3) {
                RealEstateCleanUi(realEstate).open(player)
            }
        }
    }
}