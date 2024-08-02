package io.github.hxxniverse.hobeaktown

import io.github.hxxniverse.hobeaktown.feature.area.AreaFeature
import io.github.hxxniverse.hobeaktown.feature.auction.AuctionFeature
import io.github.hxxniverse.hobeaktown.feature.bee.BeeFeature
import io.github.hxxniverse.hobeaktown.feature.coupon.CouponFeature
import io.github.hxxniverse.hobeaktown.feature.delivery_service.DeliveryServiceFeature
import io.github.hxxniverse.hobeaktown.feature.economy.EconomyFeature
import io.github.hxxniverse.hobeaktown.feature.factory.FactoryFeature
import io.github.hxxniverse.hobeaktown.feature.fatigue.FatigueFeature
import io.github.hxxniverse.hobeaktown.feature.fish.FishFeature
import io.github.hxxniverse.hobeaktown.feature.keycard.KeyCardFeature
import io.github.hxxniverse.hobeaktown.feature.mail.MailFeature
import io.github.hxxniverse.hobeaktown.feature.mainmenu.MainMenuFeature
import io.github.hxxniverse.hobeaktown.feature.nbt.NbtFeature
import io.github.hxxniverse.hobeaktown.feature.police.PoliceFeature
import io.github.hxxniverse.hobeaktown.feature.quarry.QuarryFeature
import io.github.hxxniverse.hobeaktown.feature.randombox.RandomBoxFeature
import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstateFeature
import io.github.hxxniverse.hobeaktown.feature.school.SchoolFeature
import io.github.hxxniverse.hobeaktown.feature.stock.StockFeature
import io.github.hxxniverse.hobeaktown.feature.traffic.TrafficFeature
import io.github.hxxniverse.hobeaktown.feature.user.UserFeature
import io.github.hxxniverse.hobeaktown.feature.user_trade.UserTradeFeature
import io.github.hxxniverse.hobeaktown.feature.vote.VoteFeature
import io.github.hxxniverse.hobeaktown.feature.wasteland.WastelandFeature
import io.github.hxxniverse.hobeaktown.util.command_help.help
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.monun.kommand.kommand
import kotlinx.coroutines.Job
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger

class HobeakTownPlugin : JavaPlugin() {

    companion object {
        lateinit var plugin: HobeakTownPlugin
    }

    val jobs = Job()

    private val features = mutableListOf(
        UserFeature(),
        StockFeature(),
        VoteFeature(),
        EconomyFeature(),
        RealEstateFeature(),
        FatigueFeature(),
        KeyCardFeature(),
        RandomBoxFeature(),
        DeliveryServiceFeature(),
        CouponFeature(),
        AreaFeature(),
        QuarryFeature(),
        UserTradeFeature(),
        MailFeature(),
        AuctionFeature(),
        PoliceFeature(),
        TrafficFeature(),
        FactoryFeature(),
        NbtFeature(),
        SchoolFeature(),
        WastelandFeature(),
        BeeFeature(),
        FishFeature(),
        MainMenuFeature()
    )

    override fun onEnable() {
        super.onEnable()
        plugin = this

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        plugin.kommand {
            register("호백타운") {
                executes {
                    help("호백타운") {
                        command("atm") { description = "atm 관련 명령어" }
                        command("coupon") { description = "coupon 관련 명령어" }
                        command("help") { description = "help 관련 명령어" }
                        command("mail") { description = "mail 관련 명령어" }
                        command("pos") { description = "pos 관련 명령어" }
                        command("quarry") { description = "quarry 관련 명령어" }
                        command("randombox") { description = "randombox 관련 명령어" }
                        command("real") { description = "real 관련 명령어" }
                        command("reload") { description = "reload 관련 명령어" }
                        command("stock") { description = "stock 관련 명령어" }
                        command("traffic") { description = "traffic 관련 명령어" }
                        command("usertrade") { description = "usertrade 관련 명령어" }
                        command("vote") { description = "vote 관련 명령어" }
                        command("경매장") { description = "경매장 관련 명령어" }
                        command("경찰") { description = "경찰 관련 명령어" }
                        command("주식") { description = "주식 관련 명령어" }
                        command("키카드") { description = "키카드 관련 명령어" }
                        command("태그") { description = "태그 관련 명령어" }
                        command("택배") { description = "택배 관련 명령어" }
                        command("황무지") { description = "황무지 관련 명령어 " }
                        command("양봉") { description = "양봉 관련 명령어 "}
                        command("낚시") { description = "낚시 관련 명령어" }
                        command("메인메뉴") { description = "메인메뉴 관련 명령어" }
                    }
                }
            }
        }

        Database.connect("jdbc:sqlite:${dataFolder.path}/hobeaktown.db", "org.sqlite.JDBC")

        loggedTransaction {
            addLogger(StdOutSqlLogger)
        }

        features.forEach {
            it.onEnable(this)
        }
    }

    override fun onDisable() {
        features.forEach {
            it.onDisable(this)
        }
        jobs.cancel()
        super.onDisable()
    }
}