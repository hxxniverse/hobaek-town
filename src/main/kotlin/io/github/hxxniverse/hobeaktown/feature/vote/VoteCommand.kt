package io.github.hxxniverse.hobeaktown.feature.vote

import io.github.hxxniverse.hobeaktown.feature.vote.entity.Vote
import io.github.hxxniverse.hobeaktown.feature.vote.entity.VoteHistories
import io.github.hxxniverse.hobeaktown.feature.vote.entity.VoteHistory
import io.github.hxxniverse.hobeaktown.feature.vote.ui.VoteStatusUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.command_help.help
import io.github.hxxniverse.hobeaktown.util.extension.component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import io.github.hxxniverse.hobeaktown.util.database.loggedTransaction
import io.github.hxxniverse.hobeaktown.util.extension.sendErrorMessage
import io.github.hxxniverse.hobeaktown.util.extension.sendInfoMessage
import io.github.monun.kommand.*
import io.github.monun.kommand.node.KommandNode

class VoteCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("vote") {
                requires { sender.isOp }
                then("help") {
                    executes {
                        help("vote") {
                            command("vote create <question>") {
                                description = "투표 주제 생성"
                            }
                            command("vote option <id> add <option>") {
                                description = "투표 주제에 선택지 추가"
                            }
                            command("vote option <id> remove <option>") {
                                description = "투표 주제에 선택지 제거"
                            }
                            command("vote delete <id>") {
                                description = "투표 주제 삭제"
                            }
                            command("vote list") {
                                description = "투표 주제 목록"
                            }
                            command("vote start <id>") {
                                description = "투표 시작"
                            }
                            command("vote voting_booth <id>") {
                                description = "투표 부스 설정"
                            }
                            command("vote vote_box <id>") {
                                description = "투표함 설정"
                            }
                            command("vote status <id>") {
                                description = "투표 상태 확인"
                            }
                            command("vote result <id>") {
                                description = "투표 결과 확인"
                            }
                            command("vote announcement <id>") {
                                description = "투표 알림"
                            }
                            command("vote reset <id>") {
                                description = "투표 초기화"
                            }
                        }
                    }
                }
                then("create") {
                    then("question" to string(StringType.QUOTABLE_PHRASE)) {
                        executes {
                            val question: String by it

                            loggedTransaction {
                                Vote.new(question, listOf())
                            }

                            sender.sendInfoMessage("투표 주제가 생성되었습니다.")
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

                                    addVoteOption(id, option)
                                }
                            }
                        }

                        then("remove") {
                            then("option" to string(StringType.QUOTABLE_PHRASE)) {
                                executes {
                                    val id: Int by it
                                    val option: String by it

                                    removeVoteOption(id, option)
                                }
                            }
                        }
                    }
                }
                then("delete") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            deleteVote(id)
                        }
                    }
                }
                then("list") {
                    executes {
                        showVoteList()
                    }
                }
                then("start") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            startVote(id)
                        }
                    }
                }
                then("voting_booth") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            setVotingBooth(id)
                        }
                    }
                }
                then("vote_box") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            setVoteBox(id)
                        }
                    }
                }
                then("status") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            openVoteStatusUi(id)
                        }
                    }
                }
                then("result") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            stopVoteResultUi(id)
                        }
                    }
                }
                then("announcement") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            announceVote(id)
                        }
                    }
                }
                then("reset") {
                    then("id" to int()) {
                        executes {
                            val id: Int by it

                            resetVote(id)
                        }
                    }
                }
            }
        }
    }

    private fun KommandNode.vote(): KommandArgument<Vote> = dynamic { context, input ->
        val id = input.toIntOrNull() ?: return@dynamic null
        loggedTransaction { Vote.findById(id) }
    }.apply {
        suggests {
        }
    }

    private fun KommandSource.resetVote(id: Int) {
        loggedTransaction {
            val vote = Vote.findById(id) ?: return@loggedTransaction

            VoteHistory.find { VoteHistories.vote eq vote.id }.forEach { history ->
                history.delete()
            }
        }

        sender.sendInfoMessage("투표가 초기화되었습니다.")
    }

    private fun KommandSource.announceVote(id: Int) {
        val vote = loggedTransaction { Vote.findById(id) }

        if (vote == null) {
            sender.sendErrorMessage("투표가 존재하지 않습니다.")
            return
        }

        Bukkit.getOnlinePlayers().forEach { player ->
            player.showTitle(
                Title.title(
                    vote.question.component(),
                    "투표가 진행중입니다. 투표 해주세요".component(),
                )
            )
        }
    }

    private fun stopVoteResultUi(id: Int) {
        loggedTransaction {
            val vote = Vote.findById(id) ?: return@loggedTransaction
            val voteOptions = vote.options.split(",")
            val histories = VoteHistory.find { VoteHistories.vote eq vote.id }.toList()
            val optionByCount = histories.groupBy { history -> history.option }
                .mapValues { (_, histories) -> histories.size }

            vote.isVoting = false

            Bukkit.broadcast("${vote.question} 투표 결과".component())
            optionByCount.entries.sortedBy { (_, count) -> count }
                .forEach { (option, count) ->
                    Bukkit.broadcast("[${voteOptions[option]}] $count 득표".component())
                }
        }
    }

    private fun KommandSource.openVoteStatusUi(id: Int) {
        val vote = loggedTransaction { Vote.findById(id) }

        if (vote == null) {
            sender.sendErrorMessage("투표가 존재하지 않습니다.")
            return
        }

        VoteStatusUi(vote).open(player)
    }

    private fun KommandSource.setVoteBox(id: Int) {
        loggedTransaction {
            val vote = Vote.findById(id) ?: return@loggedTransaction

            vote.voteBoxLocation = player.getTargetBlock(null, 100).location
        }

        sender.sendInfoMessage("투표함이 설정되었습니다.")
    }

    private fun KommandSource.setVotingBooth(id: Int) {
        loggedTransaction {
            val vote = Vote.findById(id) ?: return@loggedTransaction

            vote.votingBoothLocation = player.getTargetBlock(null, 100).location
        }

        sender.sendInfoMessage("투표 부스가 설정되었습니다.")
    }

    private fun KommandSource.startVote(id: Int) {
        loggedTransaction {
            val vote = Vote.findById(id) ?: return@loggedTransaction

            vote.isVoting = true
        }

        sender.sendInfoMessage("투표가 시작되었습니다.")
    }

    private fun KommandSource.showVoteList() {
        loggedTransaction {
            Vote.all().forEach { vote ->
                sender.sendInfoMessage("${vote.id.value} : ${vote.question}")
            }
        }
    }

    private fun KommandSource.deleteVote(id: Int) {
        loggedTransaction {
            Vote.findById(id)?.delete()
        }

        sender.sendInfoMessage("투표 주제가 삭제되었습니다.")
    }

    private fun KommandSource.removeVoteOption(id: Int, option: String) {
        loggedTransaction {
            val vote = Vote.findById(id) ?: return@loggedTransaction
            val options = vote.options.split(",").toMutableList()
            options.remove(option)
            vote.options = options.joinToString(",")
        }

        sender.sendInfoMessage("투표 옵션이 제거되었습니다.")
    }

    private fun KommandSource.addVoteOption(id: Int, option: String) {
        loggedTransaction {
            val vote = Vote.findById(id) ?: return@loggedTransaction
            val options = vote.options.split(",").toMutableList()
            options.add(option)
            vote.options =
                options.filter { option -> option.isNotEmpty() }.joinToString(",")
        }

        sender.sendInfoMessage("투표 옵션이 추가되었습니다.")
    }
}