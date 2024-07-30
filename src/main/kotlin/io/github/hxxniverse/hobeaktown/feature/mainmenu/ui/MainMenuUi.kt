package io.github.hxxniverse.hobeaktown.feature.mainmenu.ui

import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

class MainMenuUi : CustomInventory("메뉴", 54) {
    init {
    }

    fun displayMainMenu() {
        background(BACKGROUND)

        button(1 to 2, 2 to 3, ItemStackBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("내 정보").build()) {
            // 미정
        }

        button(1 to 5, 2 to 5, ItemStackBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplayName("택배함").build()) {
            // 택배함 플러그인 연결
        }

        button(1 to 7, 2 to 8, ItemStackBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("호백패스").build()) {
            // TODO(호백패스)
        }

        button(4 to 2, 5 to 3, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("보상").build()) {
            // TODO(보상)
        }

        button(4 to 5, 5 to 6, ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("퀘스트").build()) {
            // 퀘스트 연결 미정
        }

        button(4 to 7, 5 to 8, ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("커뮤니티").build()) {
            // TODO(커뮤니티)
        }
    }

    fun displayCommunity() {
        background(BACKGROUND)

        button(1 to 3, 2 to 4, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("디스코드").build()) {
            val url = "https://discord.com/invite/JF8H866mPR"
            val message = Component.text("디스코드")
                .color(TextColor.color(0x1F8B4C))
                .decorate(TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("클릭하여 디스코드에 접속하기")))
                .clickEvent(ClickEvent.openUrl(url))
            player.sendMessage(message)
        }

        button(1 to 6, 2 to 7, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("호백 치지직").build()) {
            // https://chzzk.naver.com/da504b5309f9b4eee7e9022690e215d1
        }

        button(4 to 3, 5 to 4, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("마인리스트").build()) {
            // https://minelist.kr/servers/hxxstella.co.kr
        }

        button(4 to 6, 5 to 7, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("인스타").build()) {
            // 인스타 미정
        }
    }
}