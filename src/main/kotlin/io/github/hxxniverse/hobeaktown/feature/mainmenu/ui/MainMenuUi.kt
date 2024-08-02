package io.github.hxxniverse.hobeaktown.feature.mainmenu.ui

import io.github.hxxniverse.hobeaktown.feature.mainmenu.entity.*
import io.github.hxxniverse.hobeaktown.util.ItemStackBuilder
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.inventory.CustomInventory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

@Suppress("DEPRECATION")
class MainMenuUi : CustomInventory("메뉴", 54) {
    init {
        displayMainMenu()
    }

    private fun displayMainMenu() {
        background(BACKGROUND)

        button(1 to 2, 2 to 3, ItemStackBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("내 정보").build()) {
            // 미정
            player.playSound(player.location, "custom.hobaek2", SoundCategory.MASTER, 1F, 1F)
        }

        button(1 to 5, 2 to 5, ItemStackBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplayName("택배함").build()) {
            // 택배함 플러그인 연결
            player.playSound(player.location, "custom.hobaek2", SoundCategory.MASTER, 1F, 1F)
        }

        button(1 to 7, 2 to 8, ItemStackBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("호백패스").build()) {
            // 호백패스
            player.playSound(player.location, "custom.hobaek2", SoundCategory.MASTER, 1F, 1F)
        }

        button(4 to 2, 5 to 3, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("보상").build()) {
            displayReward()
            player.playSound(player.location, "custom.hobaek2", SoundCategory.MASTER, 1F, 1F)
        }

        button(4 to 5, 5 to 5, ItemStackBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("퀘스트").build()) {
            // 퀘스트 연결 미정
            player.playSound(player.location, "custom.hobaek2", SoundCategory.MASTER, 1F, 1F)
        }

        button(4 to 7, 5 to 8, ItemStackBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("커뮤니티").build()) {
            displayCommunity()
            player.playSound(player.location, "custom.hobaek2", SoundCategory.MASTER, 1F, 1F)
        }
    }

    private fun displayReward() {
        background(BACKGROUND)

        button(2 to 2, 4 to 4, ItemStackBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("일일 보상 수령").build()) {
            displayDayReward()
        }

        button(2 to 6, 4 to 8, ItemStackBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("누적 보상 수령").build()) {
            displayTotalReward()
        }
    }

    private fun displayDayReward() {
        background(BACKGROUND)

        val positions = listOf(
            2 to 2, 2 to 4, 2 to 6, 2 to 8,
            4 to 2, 4 to 4, 4 to 6, 4 to 8
        )

        val times = listOf(
            30, 60, 180, 360,
            540, 720, 960, 1320
        )

        for ((index, pos) in positions.withIndex()) {
            val reward = loggedTransaction {
                DayReward.getReward(index)
            }.clone().apply {
                val meta = itemMeta
                val lore = meta.lore?.toMutableList() ?: mutableListOf()
                val playtime = Playtime.getDayPlaytime(player.uniqueId)

                when {
                    DayRewardClaim.hasClaimedReward(player.uniqueId, index) -> {
                        lore += listOf("", "§7오늘은 보상을 받았습니다")
                    }
                    playtime >= times[index] -> {
                        lore += listOf("", "§a보상 받기 가능")
                    }
                    else -> {
                        val remainingTime = times[index] - playtime
                        val hours = remainingTime / 60
                        val minutes = remainingTime % 60
                        val timeString = buildString {
                            if (hours > 0) append("${hours}시간 ")
                            append("${minutes}분 필요")
                        }

                        lore += listOf("", "§c보상까지 $timeString")
                    }
                }

                meta.lore = lore
                itemMeta = meta
            }

            item(pos, reward) {
                val player = it.whoClicked as Player
                val playtime = Playtime.getDayPlaytime(player.uniqueId)

                if (DayRewardClaim.hasClaimedReward(player.uniqueId, index)) {
//                    player.sendMessage("§c이미 오늘 해당 보상을 받았습니다.")
                } else if (playtime >= times[index]) {
                    val originalReward = loggedTransaction {
                        DayReward.getReward(index)
                    }

                    player.inventory.addItem(originalReward)
                    DayRewardClaim.claimReward(player.uniqueId, index)

                    reward.itemMeta = reward.itemMeta?.apply {
                        lore = listOf("", "§7오늘은 보상을 받았습니다")
                    }

                    item(pos, reward) {
                        it.isCancelled = true
                    }
                }

                it.isCancelled = true
            }
        }
    }

    private fun displayTotalReward() {
        background(BACKGROUND)

        val positions = listOf(
            2 to 2, 2 to 4, 2 to 6, 2 to 8,
            4 to 2, 4 to 4, 4 to 6, 4 to 8
        )

        val times = listOf(
            1440, 4320, 7200, 14400,
            21600, 25800, 34200, 40800
        )

        for ((index, pos) in positions.withIndex()) {
            val reward = loggedTransaction {
                TotalReward.getReward(index)
            }.clone().apply {
                val meta = itemMeta
                val lore = meta.lore?.toMutableList() ?: mutableListOf()
                val playtime = Playtime.getTotalPlaytime(player.uniqueId)

                when {
                    TotalRewardClaim.hasClaimedReward(player.uniqueId, index) -> {
                        lore += listOf("", "§7이미 해당 보상을 받았습니다")
                    }
                    playtime >= times[index] -> {
                        lore += listOf("", "§a보상 받기 가능")
                    }
                    else -> {
                        val remainingTime = times[index] - playtime
                        val days = remainingTime / 1440
                        val hours = (remainingTime % 1440) / 60
                        val minutes = remainingTime % 60
                        val timeString = buildString {
                            if(days > 0) append("${days}일 ")
                            if(hours > 0) append("${hours}시간 ")
                            append("${minutes}분 필요")
                        }

                        lore += listOf("", "§c보상까지 $timeString")
                    }
                }

                meta.lore = lore
                itemMeta = meta
            }

            item(pos, reward) {
                val player = it.whoClicked as Player
                val playtime = Playtime.getTotalPlaytime(player.uniqueId)

                if (TotalRewardClaim.hasClaimedReward(player.uniqueId, index)) {
                    //
                } else if (playtime >= times[index]) {
                    val originalReward = loggedTransaction {
                        TotalReward.getReward(index)
                    }

                    player.inventory.addItem(originalReward)
                    TotalRewardClaim.claimReward(player.uniqueId, index)

                    reward.itemMeta = reward.itemMeta?.apply {
                        lore = listOf("", "§7이미 해당 보상을 받았습니다")
                    }

                    item(pos, reward) {
                        it.isCancelled = true
                    }
                }

                it.isCancelled = true
            }
        }
    }
    private fun displayCommunity() {
        background(BACKGROUND)

        button(1 to 3, 2 to 4, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("디스코드").build()) {
            val url = "https://discord.com/invite/JF8H866mPR"
            val message = Component.text("클릭하여 디스코드 접속하기")
                .color(TextColor.color(0x5865F2))
                .decorate(TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("클릭하여 접속")))
                .clickEvent(ClickEvent.openUrl(url))
            player.sendMessage(message)
            player.closeInventory()
        }

        button(1 to 6, 2 to 7, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("호백 치지직").build()) {
            val url = "https://chzzk.naver.com/da504b5309f9b4eee7e9022690e215d1"
            val message = Component.text("클릭하여 호백 치지직 접속하기")
                .color(TextColor.color(0x03C75A))
                .decorate(TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("클릭하여 접속")))
                .clickEvent(ClickEvent.openUrl(url))
            player.sendMessage(message)
            player.closeInventory()
            // https://chzzk.naver.com/da504b5309f9b4eee7e9022690e215d1
        }

        button(4 to 3, 5 to 4, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("마인리스트").build()) {
            val url = "https://minelist.kr/servers/hxxstella.co.kr"
            val message = Component.text("클릭하여 마인리스트 접속하기")
                .color(TextColor.color(0x6B3FA0))
                .decorate(TextDecoration.BOLD)
                .hoverEvent(HoverEvent.showText(Component.text("클릭하여 접속")))
                .clickEvent(ClickEvent.openUrl(url))
            player.sendMessage(message)
            player.closeInventory()
            // https://minelist.kr/servers/hxxstella.co.kr
        }

        button(4 to 6, 5 to 7, ItemStackBuilder(Material.GREEN_STAINED_GLASS_PANE).setDisplayName("인스타그램").build()) {
            // 인스타 미정
            player.closeInventory()
        }
    }
}