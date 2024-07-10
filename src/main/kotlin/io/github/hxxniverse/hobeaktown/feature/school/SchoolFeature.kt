package io.github.hxxniverse.hobeaktown.feature.school;

import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import org.bukkit.plugin.java.JavaPlugin

class SchoolFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        GradeCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {
    }
}
