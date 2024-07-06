package io.github.hxxniverse.hobeaktown

import io.github.hxxniverse.hobeaktown.feature.economy.EconomyFeature
import io.github.hxxniverse.hobeaktown.feature.fatigue.FatigueFeature
import io.github.hxxniverse.hobeaktown.feature.keycard.KeyCardFeature
import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstateFeature
import io.github.hxxniverse.hobeaktown.feature.stock.StockFeature
import io.github.hxxniverse.hobeaktown.feature.vote.VoteFeature
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database

class HobeakTownPlugin : JavaPlugin() {

    companion object {
        lateinit var plugin: JavaPlugin
    }

    private val features = mutableListOf(
        StockFeature(),
        VoteFeature(),
        EconomyFeature(),
        RealEstateFeature(),
        KeyCardFeature(),
        FatigueFeature()
    )

    override fun onEnable() {
        super.onEnable()
        plugin = this

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        plugin.kommand {
            register("hobeaktown") {
                then("customnickname") {
                    then("nickname" to string(StringType.GREEDY_PHRASE)) {
                        executes {
                            val nickname: String by it

                            if (nickname.length > 16) {
                                player.sendMessage("닉네임은 16자 이하로 입력해주세요.")
                                return@executes
                            }

                            player.customName(nickname.text())
                        }
                    }
                }
            }
        }

        Database.connect("jdbc:sqlite:${dataFolder.path}/hobeaktown.db", "org.sqlite.JDBC")

        features.forEach {
            it.onEnable(this)
        }
    }

    override fun onDisable() {
        features.forEach {
            it.onDisable(this)
        }
        super.onDisable()
    }
}