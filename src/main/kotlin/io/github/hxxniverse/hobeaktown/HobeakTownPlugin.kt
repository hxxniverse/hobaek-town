package io.github.hxxniverse.hobeaktown

import io.github.hxxniverse.hobeaktown.feature.economy.EconomyFeature
import io.github.hxxniverse.hobeaktown.feature.keycard.KeyCardFeature
import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstateFeature
import io.github.hxxniverse.hobeaktown.feature.stock.StockFeature
import io.github.hxxniverse.hobeaktown.feature.vote.VoteFeature
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
        KeyCardFeature()
    )

    override fun onEnable() {
        super.onEnable()
        plugin = this

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
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