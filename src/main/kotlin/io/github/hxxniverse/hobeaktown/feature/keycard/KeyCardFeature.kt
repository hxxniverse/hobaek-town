package io.github.hxxniverse.hobeaktown.feature.keycard

import io.github.hxxniverse.hobeaktown.feature.keycard.commands.KeyCardCommand
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin

class KeyCardFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        KeyCardCommand().register(plugin)

        plugin.server.pluginManager.registerEvents(KeyCardListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {
        plugin.logger.info("[키카드 플러그인] 키카드 역할 플러그인 종료")
    }
}

