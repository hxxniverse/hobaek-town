package io.github.hxxniverse.hobeaktown.util.extension

import org.bukkit.Location
import org.bukkit.block.Block

// pretty location
fun Location.pretty(): String {
    return "X: ${blockX}, Y: ${blockY}, Z: $blockZ"
}

fun Pair<Location, Location>.getBlockList(): List<Block> {
    val (pos1, pos2) = this
    val blockList = mutableListOf<Block>()

    for (x in pos1.blockX..pos2.blockX) {
        for (y in pos1.blockY..pos2.blockY) {
            for (z in pos1.blockZ..pos2.blockZ) {
                blockList.add(Location(pos1.world, x.toDouble(), y.toDouble(), z.toDouble()).block)
            }
        }
    }

    return blockList
}