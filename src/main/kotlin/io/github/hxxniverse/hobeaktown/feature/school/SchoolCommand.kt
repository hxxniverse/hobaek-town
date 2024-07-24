package io.github.hxxniverse.hobeaktown.feature.school;

import io.github.hxxniverse.hobeaktown.HobeakTownPlugin.Companion.plugin
import io.github.hxxniverse.hobeaktown.feature.keycard.entity.*
import io.github.hxxniverse.hobeaktown.feature.user.Job
import io.github.hxxniverse.hobeaktown.feature.user.User
import io.github.hxxniverse.hobeaktown.feature.user.Users
import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class SchoolCommand : BaseCommand {
    private val lecture = mutableMapOf<Player, SchoolData>()
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("학교") {
                requires { sender is Player }
                executes { player.sendMessage("명령어 도우미: /학교 help") }
                then("help") {
                    executes {
                        """
                        |학교 명령어
                        |  /강의시작 [강의실] [강의내용]: 수업 시작 타이틀을 출력합니다.
                        |  /강의종료: 학생 태그를 달고 있는 사람들에 수업 종료 타이틀을 출력합니다.
                        |  /문제 출제 [문제] [답]: 문제를 출제 할 수 있습니다.
                        |  /정답 [내용]: 문제에 대한 정답을 입력합니다.
                        |  /학점 확인: 본인의 학점을 확인할 수 있습니다.
                        |  *(OP) /학점 추가 [플레이어] [학점]: 학점을 임의로 줄 수 있습니다.
                        |  /졸업: 10 학점을 채우게 되면 졸업을 하여 졸업생으로 변경됩니다.
                        |  *(OP) /졸업 관리 [플레이어]: 졸업을 임의로 줄 수 있습니다. 
                        """.trimIndent().also {
                            component(it).send(player)
                        }
                    }
                }
            }
            register("강의시작") {
                requires { sender is Player }
                executes { player.sendMessage("명령어 도우미: /학교 help") }
                then("room" to string(StringType.QUOTABLE_PHRASE)) {
                    then("description" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val room: String by it
                            val description: String by it

                            loggedTransaction {
                                if(player.user.job != Job.TEACHER) {
                                    player.sendMessage(component("선생님", NamedTextColor.RED).append(component("직업만 강의를 시작할 수 있습니다.", NamedTextColor.WHITE)))
                                    return@loggedTransaction
                                }
                            }

                            if(lecture[sender as Player] != null) {
                                sender.sendMessage("두 개 이상의 강의를 동시에 진행할 수 없습니다.")
                                return@executes
                            }

                            val userForStudentAndGraduate = loggedTransaction {
                                User.find { (Users.job eq Job.STUDENT) or (Users.job eq Job.GRADUATE) or (Users.job eq Job.TEACHER) }.toList()
                            }
                            userForStudentAndGraduate.forEach { users ->
                                val userId = users.id.value
                                val player = Bukkit.getPlayer(userId) as Player
                                player.sendMessage(
                                    component("----------[학교]----------").appendNewline()
                                        .append(component("10분뒤 강의가 시작됩니다.")).appendNewline()
                                        .append(component("강사 : ").append(component(sender.name)).appendNewline())
                                        .append(component("강의실 : ").append(component(room)).appendNewline())
                                        .append(component("강의내용 : ").append(component(description)).appendNewline())
                                        .append(component("------------------------"))
                                )
                            }
                            lecture[sender as Player] = SchoolData(room, description)
                        }
                    }
                }
            }
            register("강의종료") {
                requires { sender is Player }
                executes {
                    val curLecture = lecture[sender] ?: return@executes

                    loggedTransaction {
                        if(player.user.job != Job.TEACHER) {
                            player.sendMessage(component("선생님", NamedTextColor.RED).append(component("직업만 강의를 종료할 수 있습니다.", NamedTextColor.WHITE)))
                            return@loggedTransaction
                        }
                    }

                    val userForStudentAndGraduate = loggedTransaction {
                        User.find { (Users.job eq Job.STUDENT) or (Users.job eq Job.GRADUATE) or (Users.job eq Job.TEACHER) }.toList()
                    }

                    userForStudentAndGraduate.forEach { userKeyCard ->
                        val userId = userKeyCard.id.value
                        val player = Bukkit.getPlayer(userId) as Player
                        player.sendMessage(
                            component("----------[학교]----------").appendNewline()
                                .append(component("강의가 종료되었습니다.")).appendNewline()
                                .append(component("강사 : ").append(component((sender.name)).appendNewline()))
                                .append(component("강의실 : ").append(component((curLecture.room)).appendNewline()))
                                .append(component("강의내용 : ").append(component(curLecture.description)).appendNewline())
                                .append(component("수고하셨습니다.")).appendNewline()
                                .append(component("------------------------"))
                        )
                    }
                    lecture.remove(sender);
                }
            }
            register("문제출제"){
                requires { sender is Player}
                executes { player.sendMessage("명령어 도우미: /학교 help") }
                then("question" to string(StringType.QUOTABLE_PHRASE)){
                    executes { player.sendMessage("명령어 도우미: /학교 help") }
                    then("answer" to string(StringType.QUOTABLE_PHRASE)){
                        executes {
                            val question: String by it
                            val answer: String by it

                            loggedTransaction {
                                if(player.user.job != Job.TEACHER) {
                                    player.sendMessage(component("선생님", NamedTextColor.RED).append(component("직업만 문제를 출제할 수 있습니다.", NamedTextColor.WHITE)))
                                    return@loggedTransaction
                                }
                            }

                            if(lecture[player] != null){
                                QuestionTask.startTimer(plugin)
                                lecture[player]?.question = question
                                lecture[player]?.answer = answer
                            } else player.sendMessage(component("강의", NamedTextColor.BLUE).append(component("가 진행중이지 않습니다.", NamedTextColor.WHITE)))
                        }
                    }
                }
            }
            register("정답"){
                requires { sender is Player }
                executes { player.sendMessage("명령어 도우미: /학교 help") }
                then("answer" to string(StringType.QUOTABLE_PHRASE)){
                    executes {
                        val answer: String by it

                        loggedTransaction {
                            if (player.user.job != Job.STUDENT) {
                                player.sendMessage(
                                    component("학생", NamedTextColor.RED)
                                        .append(component("직업만 정답을 입력할 수 있습니다.", NamedTextColor.WHITE)))
                                return@loggedTransaction
                            }

                            val bossBarKey = NamespacedKey(plugin, "question")
                            val existingBossBar = Bukkit.getBossBar(bossBarKey)

                            if(existingBossBar == null || !existingBossBar.players.contains(player)) {
                                    player.sendMessage("문제 풀이 중이 아닙니다.")
                                    return@loggedTransaction
                            }

                            lecture.forEach { (player, data) ->
                                if (data.answer == answer) {
                                    player.curGrade++
                                    player.sendMessage(component("정답입니다 [학점 + 1]", NamedTextColor.WHITE))
                                    existingBossBar.removePlayer(player)
                                } else player.sendMessage(component("오답입니다.", NamedTextColor.WHITE))
                            }
                        }
                    }
                }
            }
            register("학점"){
                then("추가"){
                    requires { sender.isOp }
                    then("player" to player()){
                        then("credit" to int()){
                            executes {
                                val player: Player by it
                                val credit: Int by it
                                player.curGrade = (player.curGrade + credit).coerceAtMost(player.maxGrade);
                                player.sendMessage("학점이 ${credit}점 추가되었습니다.")
                            }
                        }
                    }
                }
                then("확인"){
                    executes {
                        player.sendMessage(component("현재 학점: ", NamedTextColor.BLUE).append(component("${player.curGrade}", NamedTextColor.WHITE)))
                    }
                }
            }
            register("졸업"){
                requires { sender is Player }
                executes {
                    loggedTransaction {
                        if(player.user.job != Job.STUDENT) {
                            player.sendMessage(component("학생", NamedTextColor.RED).append(component("직업만 졸업을 할 수 있습니다.", NamedTextColor.WHITE)))
                            return@loggedTransaction
                        }
                    }
                    if(player.curGrade >= 10){
                        player.sendMessage("축하드립니다! 졸업하셨습니다!")
                        loggedTransaction {
                            player.user.job = Job.GRADUATE
                        }
                    }
                }
                then("관리"){
                    requires { sender.isOp }
                    then("player" to player()){
                        executes {
                            val player: Player by it
                            loggedTransaction {
                                player.user.job = Job.GRADUATE
                            }
                        }
                    }
                }
            }
        }
    }
}
