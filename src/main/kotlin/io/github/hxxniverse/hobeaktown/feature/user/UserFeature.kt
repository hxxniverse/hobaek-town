package io.github.hxxniverse.hobeaktown.feature.user

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class UserFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        transaction {
            SchemaUtils.drop(Users)
            SchemaUtils.create(Users)
        }
        Bukkit.getPluginManager().registerEvents(UserListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

