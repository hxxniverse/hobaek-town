package io.github.hxxniverse.hobeaktown.feature.school;

import io.github.hxxniverse.hobeaktown.feature.user.Job
import io.github.hxxniverse.hobeaktown.feature.user.user
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.hxxniverse.hobeaktown.util.extension.send
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class GradeCommand : BaseCommand {
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
                then("args" to string(StringType.GREEDY_PHRASE)) {
                    executes {
                        val args: String by it

                        val room = args.split(" ")[0]
                        val description = args.split(" ")[1]

                        Bukkit.getOnlinePlayers().filter { it.user.job == Job.STUDENT }.forEach { player ->
                            plugin.logger.info("학생 리스트: $player}")
                            player?.sendMessage(
                                component("----------[학교]----------").appendNewline()
                                    .append(component("10분뒤 강의가 시작됩니다.")).appendNewline()
                                    .append(component("강사 : ").append(component(sender.name)).appendNewline())
                                    .append(component("강의실 : ").append(component(room)).appendNewline())
                                    .append(component("강의내용 : ").append(component(description)).appendNewline())
                                    .append(component("------------------------"))
                            )
                        }
                    }
                }
            }
            register("강의종료") {
                requires { sender is Player }
                executes {
                    Bukkit.getOnlinePlayers().filter { it.user.job == Job.STUDENT }.forEach { player ->
                        player.sendMessage(
                            component("----------[학교]----------").appendNewline()
                                .append(component("강의가 종료되었습니다.")).appendNewline()
                                .append(component("강사 : "))
                                .append(component((sender.name)).appendNewline())
                                //                                .append(component("강의실 : ").append(component(room)).appendNewline())
                                //                                .append(component("강의내용 : ").append(component(description)).appendNewline())
                                .append(component("------------------------"))
                        )
                    }
                }
            }
        }
    }
}
