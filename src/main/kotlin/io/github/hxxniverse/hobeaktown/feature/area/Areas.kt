package io.github.hxxniverse.hobeaktown.feature.area

import io.github.hxxniverse.hobeaktown.util.database.location
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Location
import org.bukkit.block.Block
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import kotlin.math.max
import kotlin.math.min

enum class AreaType {
    MINE, WASTE_LAND
}

object Areas : IntIdTable() {
    val name = text("text")
    val pos1 = location("pos1")
    val pso2 = location("pos2")
    val type = enumeration<AreaType>("type")
}

class Area(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Area>(Areas)

    var name by Areas.name
    var pos1 by Areas.pos1
    var pos2 by Areas.pso2
    var type by Areas.type

    fun inArea(block: Block): Boolean = loggedTransaction {
        return@loggedTransaction block.location in pos1 to pos2
    }
}

private operator fun Pair<Location, Location>.contains(location: Location): Boolean {
    val (pos1, pos2) = this
    return location.world == pos1.world &&
            location.blockX in min(pos1.blockX, pos2.blockX)..max(pos1.blockX, pos2.blockX) &&
            location.blockY in min(pos1.blockY, pos2.blockY)..max(pos1.blockY, pos2.blockY) &&
            location.blockZ in min(pos1.blockZ, pos2.blockZ)..max(pos1.blockZ, pos2.blockZ)
}
