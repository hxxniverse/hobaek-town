package io.github.hxxniverse.hobeaktown.feature.randombox

import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxItems
import io.github.hxxniverse.hobeaktown.feature.randombox.entity.RandomBoxes
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class RandomBoxFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        transaction {
            SchemaUtils.create(RandomBoxes, RandomBoxItems)
        }
        plugin.server.pluginManager.registerEvents(RandomBoxListener(), plugin)
        RandomBoxCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }

}