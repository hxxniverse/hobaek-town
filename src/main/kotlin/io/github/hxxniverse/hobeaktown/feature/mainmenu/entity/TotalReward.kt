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

object TotalRewards : IntIdTable() {
    val index = integer("index")
    val item = itemStack("item").nullable()
}

class TotalReward(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TotalReward>(TotalRewards) {
        fun setReward(index: Int, item: ItemStack?) {
            loggedTransaction {
                val reward = TotalReward.find { TotalRewards.index eq index }.firstOrNull()
                if(reward != null) {
                    if(item == null || item.type == Material.AIR) {
                        reward.delete()
                    } else {
                        reward.item = item
                    }
                } else if(item != null && item.type != Material.AIR) {
                    TotalReward.new {
                        this.index = index
                        this.item = item
                    }
                }
            }
        }

        fun getReward(index: Int): ItemStack {
            return loggedTransaction {
                TotalReward.find { TotalRewards.index eq index }.firstOrNull()?.item ?: ItemStack(Material.AIR)
            }
        }
    }

    var index by TotalRewards.index
    var item by TotalRewards.item
}

object TotalRewardClaims : IntIdTable() {
    val player = uuid("player")
    val rewardIndex = integer("reward_index")
}

class TotalRewardClaim(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TotalRewardClaim>(TotalRewardClaims) {
        fun hasClaimedReward(uuid: UUID, index: Int): Boolean {
            return transaction {
                find { TotalRewardClaims.player eq uuid and (TotalRewardClaims.rewardIndex eq index) }.empty().not()
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
                TotalRewardClaims.deleteAll()
            }
        }
    }

    var player by TotalRewardClaims.player
    var rewardIndex by TotalRewardClaims.rewardIndex
}