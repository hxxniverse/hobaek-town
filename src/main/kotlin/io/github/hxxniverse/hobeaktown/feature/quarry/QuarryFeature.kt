package io.github.hxxniverse.hobeaktown.feature.quarry

import io.github.hxxniverse.hobeaktown.util.FeatureConfig
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.location
import io.github.hxxniverse.hobeaktown.util.edit
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import io.github.hxxniverse.hobeaktown.util.itemStack
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * ### 기획
 *
 * 돌을 파괴시 일정 확률로 돌, 석탄, 철, 금, 다이아몬드, 에매랄드 광물블럭이
 * 랜덤으로 생성되며 부불 수 있는 도구가 정해져 있습니다.
 * 추가적으로 랜덤으로 3% 확률로 강화석을 얻을 수 있습니다.
 * 직업을 광부로 설정하면 광부 전용 곡갱이를 이용 할 수 있으며
 * 직업을 다른걸로 변경하면 전용 곡갱이를 갖고 있어도 사용이 안됩니다.
 *
 * ### 기능
 *
 * 명령어 리스트 ( Command list )
 *
 * /광산 위치 설정 - pos1 pos2로 광산 을 설정 할 수 있으며 설정된 곳은
 *                         블록을 캘 수 있는 장소가 됩니다.
 * /광산 곡갱이 등록 (곡갱이 이름) - 들고 있는 곡갱이를 광산 곡갱이로 설정할 수 있습니다.
 * 광산 에서만 이용 가능합니다
 * /광산 강화석 아이템설정 - 강화석 아이템을 들고 명령어 입력시 랜덤으로 나오는 강화석으로
 * 설정 됩니다.
 */
class QuarryFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        QuarryCommand().register(plugin)
        Bukkit.getPluginManager().registerEvents(QuarryListener(), plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

object Quarries : IntIdTable() {
    val name = varchar("name", 50)
    val pos1 = location("pos1")
    val pos2 = location("pos2")
}

class Quarry(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Quarry>(Quarries)

    var name by Quarries.name
    var pos1 by Quarries.pos1
    var pos2 by Quarries.pos2

    fun inQuarry(block: Block): Boolean {
        return block.location in pos1 to pos2
    }
}

private operator fun Pair<Location, Location>.contains(location: Location): Boolean {
    val (pos1, pos2) = this
    return location.world == pos1.world &&
            location.blockX in pos1.blockX..pos2.blockX &&
            location.blockY in pos1.blockY..pos2.blockY &&
            location.blockZ in pos1.blockZ..pos2.blockZ
}

object QuarryConfig : FeatureConfig<QuarryConfigData>(
    "quarry",
    QuarryConfigData(),
    QuarryConfigData.serializer()
)


@Serializable
data class QuarryConfigData(
    val mineralProbability: QuarryMineralProbability = QuarryMineralProbability(),
    val upgradeStone: QuarryUpgradeStone = QuarryUpgradeStone(),
)


@Serializable
data class QuarryMineralProbability(
    val stone: Double = 0.6,
    val coal: Double = 0.2,
    val iron: Double = 0.13,
    val gold: Double = 0.05,
    val diamond: Double = 0.01,
    val emerald: Double = 0.01,
)


@Serializable
data class QuarryUpgradeStone(
    val itemStack: @Contextual ItemStack = itemStack {
        setType(Material.NETHER_STAR)
        setDisplayName("강화석")
    },
    val chance: Double = 0.03
)

fun ItemStack.setPickForMining() {
    edit { addPersistentData("isPickForMining", true) }
}

fun ItemStack.isPickForMining(): Boolean {
    return getPersistentData<Boolean>("isPickForMining") == true
}