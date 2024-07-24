package io.github.hxxniverse.hobeaktown.feature.wasteland.entity

import io.github.hxxniverse.hobeaktown.util.database.itemStack
import io.github.hxxniverse.hobeaktown.util.database.location
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.database.material
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object WastelandSetups : IntIdTable() {
    val code = text("code")
    val material = material("material")
}

class WastelandSetup(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WastelandSetup>(WastelandSetups) {
        fun createSetup(code: String, material: Material, rewards: Map<Int, ItemStack>) {
            loggedTransaction {
                val setup = WastelandSetup.new {
                    this.code = code
                    this.material = material
                }

                rewards.forEach { (invIndex, itemStack) ->
                    WastelandSetupReward.new {
                        this.setup = setup
                        this.invIndex = invIndex
                        this.itemstack = itemStack
                    }
                }
            }
        }

        fun getSetupByCode(code: String): WastelandSetup? {
            return loggedTransaction {
                WastelandSetup.find { WastelandSetups.code eq code }.firstOrNull()
            }
        }
    }

    var code by WastelandSetups.code
    var material by WastelandSetups.material
}

object WastelandSetupRewards : IntIdTable() {
    val setup = reference("setup", WastelandSetups, onDelete = ReferenceOption.CASCADE)
    val invIndex = integer("invIndex")
    val itemstack = itemStack("itemstack")
}

class WastelandSetupReward(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WastelandSetupReward>(WastelandSetupRewards)

    var setup by WastelandSetup referencedOn WastelandSetupRewards.setup
    var invIndex by WastelandSetupRewards.invIndex
    var itemstack by WastelandSetupRewards.itemstack
}
object Wastelands : IntIdTable() {
    val code = text("code")
    val location = location("location")
    val material = material("material")
}

class Wasteland(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Wasteland>(Wastelands) {
        fun addWasteland(code: String, location: Location, material: Material) {
            loggedTransaction {
                val wasteland = Wasteland.new {
                    this.code = code
                    this.location = location
                    this.material = material
                }

                val setup = WastelandSetup.getSetupByCode(code)
                if(setup != null) {
                    WastelandSetupReward.find {
                        WastelandSetupRewards.setup eq setup.id
                    }.forEach { setupReward ->
                        WastelandReward.new {
                            this.wasteland = wasteland
                            this.invIndex = setupReward.invIndex
                            this.itemstack = setupReward.itemstack
                        }
                    }
                }
            }
        }

        fun deleteWasteland(location: Location) {
            loggedTransaction {
                Wasteland.find { Wastelands.location eq location }.forEach { it.delete() }
            }
        }

        fun getByLocation(location: Location): Wasteland? {
            return loggedTransaction {
                Wasteland.find { Wastelands.location eq location }.firstOrNull()
            }
        }
    }

    var code by Wastelands.code
    var location by Wastelands.location
    var material by Wastelands.material

    fun getRewards(): Map<Int, ItemStack> {
        val rewards = mutableMapOf<Int, ItemStack>()
        loggedTransaction {
            WastelandReward.find {
                WastelandRewards.wasteland eq this@Wasteland.id
            }.forEach { reward ->
                rewards[reward.invIndex] = reward.itemstack
            }
        }
        return rewards
    }
}

object WastelandRewards : IntIdTable() {
    val wasteland = reference("wasteland", Wastelands, onDelete = ReferenceOption.CASCADE)
    val invIndex = integer("invIndex")
    val itemstack = itemStack("itemstack")
}

class WastelandReward(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<WastelandReward>(WastelandRewards)

    var wasteland by Wasteland referencedOn WastelandRewards.wasteland
    var invIndex by WastelandRewards.invIndex
    var itemstack by WastelandRewards.itemstack
}