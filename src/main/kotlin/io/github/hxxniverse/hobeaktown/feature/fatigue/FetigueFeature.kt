package io.github.hxxniverse.hobeaktown.feature.fatigue

import io.github.hxxniverse.hobeaktown.feature.fatigue.commands.FetigueCommand
import io.github.hxxniverse.hobeaktown.feature.fatigue.entity.UserFetigue
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import javax.xml.validation.Schema

class FetigueFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        transaction {
            SchemaUtils.create(UserFetigue)
        }
        FetigueConfig.load()
        FetigueCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {
    }
}
