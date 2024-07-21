package io.github.hxxniverse.hobeaktown.feature.vote

import io.github.hxxniverse.hobeaktown.feature.vote.entity.VoteHistories
import io.github.hxxniverse.hobeaktown.feature.vote.entity.Votes
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction

class VoteFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(Votes, VoteHistories)
        }

        plugin.server.pluginManager.registerEvents(VoteListener(), plugin)
        VoteCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}