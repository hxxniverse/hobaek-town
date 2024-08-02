package io.github.hxxniverse.hobeaktown.feature.mainmenu.entity

import io.github.hxxniverse.hobeaktown.util.database.itemStack
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object DayRewards : IntIdTable() {
    val index = integer("index")
    val item = itemStack("item").nullable()
}

class DayReward(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DayReward>(DayRewards) {
        fun setReward(index: Int, item: ItemStack?) {
            loggedTransaction {
                val reward = DayReward.find { DayRewards.index eq index }.firstOrNull()
                if (reward != null) {
                    if(item == null || item.type == Material.AIR) {
                        reward.delete()
                    } else {
                        reward.item = item
                    }
                } else if(item != null && item.type != Material.AIR) {
                    DayReward.new {
                        this.index = index
                        this.item = item
                    }
                }
            }
        }

        fun getReward(index: Int): ItemStack {
            return loggedTransaction {
                DayReward.find { DayRewards.index eq index }.firstOrNull()?.item ?: ItemStack(Material.AIR)
            }
        }
    }

    var index by DayRewards.index
    var item by DayRewards.item
}

object DayRewardClaims : IntIdTable() {
    val player = uuid("player")
    val rewardIndex = integer("reward_index")
}

class DayRewardClaim(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DayRewardClaim>(DayRewardClaims) {
        fun hasClaimedReward(uuid: UUID, index: Int): Boolean {
            return transaction {
                find { DayRewardClaims.player eq uuid and (DayRewardClaims.rewardIndex eq index) }.empty().not()
            }
        }

        fun claimReward(uuid: UUID, index: Int) {
            transaction {
                new {
                    this.player = uuid
                    this.rewardIndex = index
                }
            }
        }

        fun resetAllClaims() {
            transaction {
                DayRewardClaims.deleteAll()
            }
        }
    }

    var player by DayRewardClaims.player
    var rewardIndex by DayRewardClaims.rewardIndex
}