package io.github.hxxniverse.hobeaktown.util.command_help

import io.github.monun.kommand.KommandSource
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHelp(
    val title: String
) {
    private val commands = mutableListOf<CommandLine>()

    fun command(command: String, block: CommandLine.() -> Unit) {
        commands.add(CommandLine(command).apply(block))
    }

    fun build(player: CommandSender) {
        player.sendMessage("§6§l[도움말] §f$title")
        player.sendMessage("")
        commands.forEach { command ->
            player.sendMessage("§6/${command.name} §f- ${command.description}")
            if (command.subDescription.isNotEmpty())
                player.sendMessage("§7ㄴ${command.subDescription}")
        }
    }
}

class CommandLine(
    val name: String
) {
    var description: String = ""
    var subDescription: String = ""
}

fun KommandSource.help(title: String, function: CommandHelp.() -> Unit) {
    CommandHelp(title).apply(function).build(sender)
}