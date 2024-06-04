package io.github.hxxniverse.hobeaktown.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

fun emptyLocation(world: World = Bukkit.getWorld("world")!!): Location {
    return Location(world, 0.0, 0.0, 0.0)
}