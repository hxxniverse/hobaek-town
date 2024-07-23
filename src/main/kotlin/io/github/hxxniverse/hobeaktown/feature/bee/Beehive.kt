package io.github.hxxniverse.hobeaktown.feature.bee

import io.github.hxxniverse.hobeaktown.util.database.itemStack
import io.github.hxxniverse.hobeaktown.util.database.location
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

object BeehiveSetups : IntIdTable() {
    val code = text("code")
}

class BeehiveSetup(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BeehiveSetup>(BeehiveSetups) {
        fun createSetup(code: String, items: Map<Int, ItemStack>) {
            loggedTransaction {
                val setup = BeehiveSetup.new {
                    this.code = code
                }

                items.forEach { (invId, item) ->
                    BeehiveSetupItem.new {
                        this.setup = setup // 이 부분에서 명확하게 지정
                        this.invId = invId
                        this.item = item;
                    }
                }
            }
        }

        fun getSetupByCode(code: String): BeehiveSetup? {
            return loggedTransaction {
                BeehiveSetup.find { BeehiveSetups.code eq code }.firstOrNull()
            }
        }

        fun getItemsByCode(code: String): Map<Int, ItemStack> {
            return loggedTransaction {
                val setup = BeehiveSetup.find {
                    BeehiveSetups.code eq code
                }.firstOrNull()

                val items = mutableMapOf<Int, ItemStack>()

                if (setup != null) {
                    BeehiveSetupItem.find {
                        BeehiveSetupItems.setup eq setup.id
                    }.forEach {
                            item -> items[item.invId] = item.item
                    }
                }

                items
            }
        }
    }

    var code by BeehiveSetups.code
}

object BeehiveSetupItems : IntIdTable() {
    val setup = reference("setup", BeehiveSetups, onDelete = ReferenceOption.CASCADE)
    val invId = integer("invId")
    val item = itemStack("item")
}

class BeehiveSetupItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BeehiveSetupItem>(BeehiveSetupItems)

    var setup by BeehiveSetup referencedOn BeehiveSetupItems.setup
    var invId by BeehiveSetupItems.invId
    var item by BeehiveSetupItems.item
}

object Beehives : IntIdTable() {
    val ownerUUID = uuid("owner").nullable()
    val location = location("location")
    val startTime = datetime("start_time")
    val started = bool("started").default(false)
    val code = text("code")
}

class Beehive(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Beehive>(Beehives) {
        fun createBeehive(code: String, location: Location, uuid: UUID?, rewards: Map<Int, ItemStack>) {
            loggedTransaction {
                // 이미 해당 location 에 벌통 정보가 존재하는 경우 지우기
                val existingBeehive = Beehive.find {
                    Beehives.location eq location
                }.firstOrNull()
                existingBeehive?.delete()

                val beehive = Beehive.new {
                    this.ownerUUID = uuid
                    this.location = location
                    this.startTime = LocalDateTime.now()
                    this.code = code
                }

                rewards.forEach {
                        reward -> BeehiveReward.new {
                    this.beehive = beehive
                    this.invId = reward.key
                    this.reward = reward.value
                    }
                }
            }
        }

        fun deleteBeehive(location: Location) {
            loggedTransaction {
                Beehive.find {
                    Beehives.location eq location
                }.forEach { it.delete() }
            }
        }

        fun findOwner(location: Location) : UUID? {
            return loggedTransaction {
                Beehive.find {
                    Beehives.location eq location
                }.firstOrNull()?.ownerUUID
            }
        }

        fun getByLocation(loc: Location): Beehive? {
            return loggedTransaction {
                Beehive.find {
                    Beehives.location eq loc
                }.firstOrNull()
            }
        }

        fun getRewards(location: Location): Map<Int, ItemStack> {
            val rewards = mutableMapOf<Int, ItemStack>()

            loggedTransaction {
                val beehive = Beehive.find {
                    Beehives.location eq location
                }.firstOrNull()

                if (beehive != null) {
                    BeehiveReward.find {
                        BeehiveRewards.beehive eq beehive.id
                    }.forEach {
                            reward -> rewards[reward.invId] = reward.reward
                    }
                }
            }

            return rewards
        }

        fun getAllCodes(): Set<String> {
            return loggedTransaction {
                val beehiveCodes = Beehive.all().map { it.code }
                val beehiveSetupCodes = BeehiveSetup.all().map { it.code }
                (beehiveCodes + beehiveSetupCodes).toSet()
            }
        }

        fun existCode(code: String): Boolean {
            return loggedTransaction {
                // BeehiveSetup에서 해당 코드가 있는지 확인
                val setupExists = BeehiveSetup.find { BeehiveSetups.code eq code }.firstOrNull() != null

                // Beehive에서 해당 코드가 있는지 확인
                val beehiveExists = Beehive.find { Beehives.code eq code }.firstOrNull() != null

                setupExists || beehiveExists
            }
        }

        fun editRewards(code: String, map: Map<Int, ItemStack>) {
            loggedTransaction {
                // 해당 Code 와 일치하는 모든 벌통 보상 제거
                val setup = BeehiveSetup.find {
                    BeehiveSetups.code eq code
                }.firstOrNull()

                setup?.let {
                    BeehiveSetupItem.find {
                        BeehiveSetupItems.setup eq it.id
                    }.forEach {
                        it.delete()
                    }

                    // 새로운 보상으로 대체
                    map.forEach { (invId, item) ->
                        BeehiveSetupItem.new {
                            this.setup = it
                            this.invId = invId
                            this.item = item
                        }
                    }
                }

                // 해당 코드의 모든 Beehive 보상 제거 및 새로운 보상으로 대체
                val beehives = Beehive.find { Beehives.code eq code }
                beehives.forEach { beehive ->
                    BeehiveReward.find { BeehiveRewards.beehive eq beehive.id }.forEach { it.delete() }
                    map.forEach { (invId, item) ->
                        BeehiveReward.new {
                            this.beehive = beehive
                            this.invId = invId
                            this.reward = item
                        }
                    }
                }
            }
        }
    }

    var ownerUUID by Beehives.ownerUUID
    var location by Beehives.location
    var startTime by Beehives.startTime
    var started by Beehives.started
    var code by Beehives.code

    fun start(who: UUID) {
        started = true
        startTime = LocalDateTime.now()
        ownerUUID = who
    }

    fun isFinish() : Boolean {
        return LocalDateTime.now().isAfter(startTime.plusSeconds(BeeFeature.BEEHIVE_DURATION_SECOND.toLong()))
    }

    fun reset() {
        started = false
        ownerUUID = null
    }
}

object BeehiveRewards : IntIdTable() {
    val beehive = reference("beehive", Beehives, onDelete = ReferenceOption.CASCADE)
    val invId = integer("invId")
    val reward = itemStack("reward")
}

class BeehiveReward(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BeehiveReward>(BeehiveRewards)

    var beehive by Beehive referencedOn BeehiveRewards.beehive
    var invId by BeehiveRewards.invId
    var reward by BeehiveRewards.reward
}
