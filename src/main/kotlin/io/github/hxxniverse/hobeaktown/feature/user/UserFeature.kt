package io.github.hxxniverse.hobeaktown.feature.user

import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.base.BaseFeature
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.getPersistentData
import io.github.hxxniverse.hobeaktown.util.itemStack
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.StringType
import io.github.monun.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.SchemaUtils

class UserFeature : BaseFeature {
    override fun onEnable(plugin: JavaPlugin) {
        loggedTransaction {
            SchemaUtils.create(Users)
        }
        Bukkit.getPluginManager().registerEvents(UserListener(), plugin)
        UserCommand().register(plugin)
    }

    override fun onDisable(plugin: JavaPlugin) {

    }
}

class UserCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("신분증") {
                then("발급") {
                    then(
                        "name" to string(StringType.QUOTABLE_PHRASE),
                        "age" to int(),
                        "specialNote" to string(StringType.QUOTABLE_PHRASE)
                    ) {
                        executes {
                            createIdentificationCard(it["name"], it["age"], it["specialNote"])
                        }
                    }
                }
                then("수정") {
                    then(
                        "name" to string(StringType.QUOTABLE_PHRASE), "age" to int(),
                        "specialNote" to string(StringType.QUOTABLE_PHRASE)
                    ) {
                        executes {
                            updateIdentificationCard(it["name"], it["age"], it["specialNote"])
                        }
                    }
                }
            }
            register("벌점") {
                // 추가 설정 초기화 확인
                then("추가") {
                    then("target" to player(), "points" to int()) {
                        executes {
                            addPenaltyPoints(it["target"], it["points"])
                        }
                    }
                }
                then("감소") {
                    then("target" to player(), "points" to int()) {
                        executes {
                            removePenaltyPoints(it["target"], it["points"])
                        }
                    }
                }
                then("설정") {
                    then("target" to player(), "points" to int()) {
                        executes {
                            setPenaltyPoints(it["target"], it["points"])
                        }
                    }
                }
                then("초기화") {
                    then("target" to player()) {
                        executes {
                            resetPenaltyPoints(it["target"])
                        }
                    }
                }
            }
        }
    }

    private fun KommandSource.addPenaltyPoints(target: Player, points: Int) {
        target.user.penaltyPoints += points
        target.sendMessage("§a벌점이 추가되었습니다.")
        sender.sendMessage("§a${target.name} 님에게 벌점이 추가되었습니다. (현재: ${target.user.penaltyPoints}점")
    }

    private fun KommandSource.removePenaltyPoints(target: Player, points: Int) {
        target.user.penaltyPoints -= points
        target.sendMessage("§a벌점이 감소되었습니다.")
        sender.sendMessage("§a${target.name} 님에게 벌점이 감소되었습니다. (현재: ${target.user.penaltyPoints}점")
    }

    private fun KommandSource.setPenaltyPoints(target: Player, points: Int) {
        target.user.penaltyPoints = points
        target.sendMessage("§a벌점이 설정되었습니다.")
        sender.sendMessage("§a${target.name} 님의 벌점이 설정되었습니다. (현재: ${target.user.penaltyPoints}점")
    }

    private fun KommandSource.resetPenaltyPoints(target: Player) {
        target.user.penaltyPoints = 0
        target.sendMessage("§a벌점이 초기화되었습니다.")
        sender.sendMessage("§a${target.name} 님의 벌점이 초기화되었습니다.")
    }

    private fun KommandSource.createIdentificationCard(name: String, age: Int, specialNote: String) {
        val user = loggedTransaction {
            User.new {
                this.name = name
                this.age = age
                this.specialNote = specialNote
                this.penaltyPoints = 0
                this.job = Job.CITIZEN
            }
        }

        val identificationCard = IdentificationCard(user)
        player.inventory.addItem(identificationCard.createItem())
    }

    private fun KommandSource.updateIdentificationCard(name: String, age: Int, specialNote: String) {
        if (!player.inventory.itemInMainHand.isIdentificationCard()) {
            player.sendMessage("§c신분증을 들고 명령어를 입력해주세요.")
            return
        }

        loggedTransaction {
            player.user.name = name
            player.user.age = age
            player.user.specialNote = specialNote
            player.user.penaltyPoints = 0
            player.user.job = Job.CITIZEN
        }

        IdentificationCard(player.user).createItem()
            .also { player.inventory.setItemInMainHand(it) }
        player.sendMessage("§a신분증이 수정되었습니다.")
    }
}


class IdentificationCard(val user: User) {
    fun createItem(): ItemStack {
        return itemStack {
            displayName = "§f§l${user.name}의 신분증".component()
            lore = listOf(
                "§7우클릭 시 자세한 정보를 볼 수 있습니다.".component()
            )
            addPersistentData("user", user.id.toString())
        }
    }
}

fun ItemStack.isIdentificationCard(): Boolean {
    return getPersistentData<String>("user") != null
}