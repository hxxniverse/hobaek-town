package io.github.hxxniverse.hobeaktown.feature.fatigue.entity;
import io.github.hxxniverse.hobeaktown.feature.fatigue.config.AreaFatigueConfig
import io.github.hxxniverse.hobeaktown.feature.real_estate.RealEstateType
import io.github.hxxniverse.hobeaktown.util.database.location
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.max
import kotlin.math.min

object FatigueAreas : IntIdTable() {
    val name = varchar("name", 255)
    val isMinus = bool("is_minus").default(true)
    val fatigue = integer("fatigue").default(0)
    val cycle = integer("cycle").default(0)
    val pos1 = location("pos1")
    val pos2 = location("pos2")
}

class FatigueArea(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<FatigueArea>(FatigueAreas) {
        fun create(
            name: String,
            pos1: Location,
            pos2: Location,
        ) = transaction {
            FatigueArea.new {
                this.name = name
                this.fatigue = AreaFatigueConfig.configData.initialFatigue
                this.cycle = AreaFatigueConfig.configData.initialCycle
                this.pos1 = Location(pos1.world, min(pos1.x, pos2.x), 0.0, min(pos1.z, pos2.z))
                this.pos2 = Location(pos2.world, max(pos1.x, pos2.x), 255.0, max(pos1.z, pos2.z))
            }
        }

        fun list(player: Player) {
            player.sendMessage("피로도 구역 목록")
            val message = StringBuilder()
            transaction {
                FatigueAreas.selectAll().forEach {
                    val name = it[FatigueAreas.name]
                    val pos1 = it[FatigueAreas.pos1]
                    val isMinus = if (it[FatigueAreas.isMinus]) "감소" else "증가"
                    val fatigue = it[FatigueAreas.fatigue]
                    val cycle = it[FatigueAreas.cycle]

                    message.append("이름: $name \n")
                    message.append("좌표: ${pos1.x}, ${pos1.y}, ${pos1.z} \n")
                    message.append("피로도: $fatigue $isMinus\n")
                    message.append("적용 주기: $cycle 분\n")
                    message.append("\n")
                }
            }
            player.sendMessage(message.toString())
        }
        fun setAreaFatigue(
            name: String,
            fatigue: Int,
            cycle: Int,
            isMinus: Boolean
        ): Boolean = transaction {
            val area = FatigueArea.find { FatigueAreas.name eq name }.firstOrNull() ?: return@transaction false
            area.fatigue = fatigue
            area.cycle = cycle
            area.isMinus = isMinus

            return@transaction true;
        }
    }
    fun isInside(location: Location): Boolean = transaction {
        return@transaction location.x.toInt() in pos1.x.toInt()..pos2.x.toInt() &&
                location.y.toInt() in pos1.y.toInt()..pos2.y.toInt() &&
                location.z.toInt() in pos1.z.toInt()..pos2.z.toInt()
    }

    var name by FatigueAreas.name
    var isMinus by FatigueAreas.isMinus
    var fatigue by FatigueAreas.fatigue
    var cycle by FatigueAreas.cycle
    var pos1 by FatigueAreas.pos1
    var pos2 by FatigueAreas.pos2
}
