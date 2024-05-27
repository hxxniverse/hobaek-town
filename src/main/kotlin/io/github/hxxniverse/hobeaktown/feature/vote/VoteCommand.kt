package io.github.hxxniverse.hobeaktown.feature.vote

import io.github.hxxniverse.hobeaktown.feature.vote.entity.Vote
import io.github.hxxniverse.hobeaktown.feature.vote.entity.VoteHistories
import io.github.hxxniverse.hobeaktown.feature.vote.entity.VoteHistory
import io.github.hxxniverse.hobeaktown.feature.vote.ui.VoteStatusUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction

class VoteCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("vote") {
                executes {
                    sender.sendMessage("투표")
                    sender.sendMessage("* /vote create [question] - \"투표 주제\" 를 생성합니다.")
                    sender.sendMessage("* /vote option [id] add [option] - \"투표 주제\" 에 선택지를 추가합니다.")
                    sender.sendMessage("* /vote option [id] remove [option] - \"투표 주제\" 에 선택지를 제거합니다.")
                    sender.sendMessage("* /vote voting_booth [id] - 바라보는 블럭(100칸 이내)을 기표소로 설정합니다.")
                    sender.sendMessage("* /vote vote_box [id] - 바라보는 블럭(100칸 이내)을 투표함으로 설정합니다.")
                    sender.sendMessage("* /vote status [id] - 투표 상태를 확인합니다.")
                    sender.sendMessage("* /vote result [id] - 투표 결과를 확인합니다.")
                    sender.sendMessage("* /vote announcement [id] - 투표를 알립니다.")
                    sender.sendMessage("* /vote reset [id] - 투표를 초기화합니다.")
                }
                then("create") {
                    then("question" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val question: String by it

                            transaction {
                                Vote.new(question, listOf())
                            }

                            sender.sendMessage("투표 주제가 생성되었습니다.")
                        }
                    }
                }
                then("option") {
                    then("id" to int()) {
                        then("add") {
                            then("option" to string(StringType.QUOTABLE_PHRASE)) {
                                executes {
                                    val id: Int by it
                                    val option: String by it

                                    transaction {
                                        val vote = Vote.findById(id) ?: return@transaction
                                        val options = vote.options.split(",").toMutableList()
                                        options.add(option)
                                        vote.options = options.joinToString(",")
                                    }

                                    sender.sendMessage("투표 옵션이 추가되었습니다.")
                                }
                            }
                        }

                        then("remove") {
                            then("option" to string(StringType.QUOTABLE_PHRASE)) {
                                executes {
                                    val id: Int by it
                                    val option: String by it

                                    transaction {
                                        val vote = Vote.findById(id) ?: return@transaction
                                        val options = vote.options.split(",").toMutableList()
                                        options.remove(option)
                                        vote.options = options.joinToString(",")
                                    }

                                    sender.sendMessage("투표 옵션이 제거되었습니다.")
                                }
                            }
                        }
                    }
                }
                then("delete") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            transaction {
                                Vote.findById(id)?.delete()
                            }

                            sender.sendMessage("투표 주제가 삭제되었습니다.")
                        }
                    }
                }
                then("list") {
                    executes {
                        transaction {
                            Vote.all().forEach { vote ->
                                sender.sendMessage("${vote.id.value} : ${vote.question}")
                            }
                        }
                    }
                }
                then("start") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            transaction {
                                val vote = Vote.findById(id) ?: return@transaction

                                vote.isVoting = true
                            }

                            sender.sendMessage("투표가 시작되었습니다.")
                        }
                    }
                }
                then("voting_booth") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            transaction {
                                val vote = Vote.findById(id) ?: return@transaction

                                vote.votingBoothLocation = player.getTargetBlock(null, 100).location
                            }

                            sender.sendMessage("투표 부스가 설정되었습니다.")
                        }
                    }
                }
                then("vote_box") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            transaction {
                                val vote = Vote.findById(id) ?: return@transaction

                                vote.voteBoxLocation = player.getTargetBlock(null, 100).location
                            }

                            sender.sendMessage("투표함이 설정되었습니다.")
                        }
                    }
                }
                then("status") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            val vote = transaction { Vote.findById(id) }

                            if (vote == null) {
                                sender.sendMessage("투표가 존재하지 않습니다.")
                                return@executes
                            }

                            VoteStatusUi(vote).open(player)
                        }
                    }
                }
                then("result") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            transaction {
                                val vote = Vote.findById(id) ?: return@transaction
                                val voteOptions = vote.options.split(",")
                                val histories = VoteHistory.find { VoteHistories.vote eq vote.id }.toList()
                                val optionByCount = histories.groupBy { history -> history.option }
                                    .mapValues { (_, histories) -> histories.size }

                                vote.isVoting = false

                                Bukkit.broadcast("${vote.question} 투표 결과".text())
                                optionByCount.entries.sortedBy { (_, count) -> count }
                                    .forEach { (option, count) ->
                                        Bukkit.broadcast("[${voteOptions[option]}] $count 득표".text())
                                    }
                            }
                        }
                    }
                }
                then("announcement") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            val vote = transaction { Vote.findById(id) }

                            if (vote == null) {
                                sender.sendMessage("투표가 존재하지 않습니다.")
                                return@executes
                            }

                            Bukkit.getOnlinePlayers().forEach { player ->
                                player.showTitle(
                                    Title.title(
                                        vote.question.text(),
                                        "투표가 진행중입니다. 투표 해주세요".text(),
                                    )
                                )
                            }
                        }
                    }
                }
                then("reset") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            transaction {
                                val vote = Vote.findById(id) ?: return@transaction

                                VoteHistory.find { VoteHistories.vote eq vote.id }.forEach { history ->
                                    history.delete()
                                }
                            }

                            sender.sendMessage("투표가 초기화되었습니다.")
                        }
                    }
                }
            }
        }
    }
}