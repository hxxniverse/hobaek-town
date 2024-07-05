package io.github.hxxniverse.hobeaktown.feature.stock

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stocks
import io.github.hxxniverse.hobeaktown.feature.stock.ui.StockStatusUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.component
import io.github.monun.kommand.KommandArgument.Companion.dynamic
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction

class StockCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("stock", "주식") {
                executes {
                    val player = sender as? Player
                    if (player == null) {
                        sender.sendMessage("플레이어만 사용할 수 있는 명령어입니다.")
                        return@executes
                    }
                    StockStatusUi().open(player)
                }
                requires { sender.isOp }
                then("help") {
                    executes {
                        sender.sendMessage("주식")
                        sender.sendMessage("* /stock item status - 주식 종목을 확인합니다.")
                        sender.sendMessage("* /stock item create [stockName] [amount] [price] [fluctuation] - 주식 종목을 추가합니다.")
                        sender.sendMessage("* /stock item delete [stock] - 주식 종목을 삭제합니다.")
                        sender.sendMessage("* /stock item update [stock] [amount] [price] [fluctuation] - 주식 종목을 수정합니다.")
                        sender.sendMessage("* /stock fluctuation [time] - 주식 변동 시간을 설정합니다.")
                    }
                }
                then("item") {
                    then("status") {
                        executes {
                            val stocks = transaction {
                                Stock.all()
                                    .joinToString("\n") { "${it.name}\t| ${it.remainingAmount} \t| ${it.currentPrice} \t| ${it.beforePrice - it.currentPrice}" }
                            }

                            val leftInterval = PriceChangeTask.getLeftInterval() / 1000
                            val minute = leftInterval / 60
                            val second = leftInterval % 60

                            sender.sendMessage("주식 변동까지 남은시간: ${minute}분 ${second}초")
                            sender.sendMessage("이름\t| 판매개수\t| 가격\t| 변동폭")
                            stocks.split("\n").forEach {
                                sender.sendMessage(it)
                            }
                        }
                    }
                    then("create") {
                        then("stockName" to string(type = StringType.QUOTABLE_PHRASE)) {
                            then("amount" to int()) {
                                then("price" to int()) {
                                    then("fluctuation" to int()) {
                                        executes {
                                            val stockName: String by it
                                            val amount: Int by it
                                            val price: Int by it
                                            val fluctuation: Int by it

                                            if (transaction {
                                                    Stock.find { Stocks.name eq stockName }.firstOrNull()
                                                } != null
                                            ) {
                                                sender.sendMessage("이미 존재하는 주식 종목입니다.")
                                                return@executes
                                            }

                                            val stock = transaction {
                                                Stock.new(
                                                    name = stockName,
                                                    remainingAmount = amount,
                                                    currentPrice = price,
                                                    fluctuation = fluctuation,
                                                )
                                            }

                                            sender.sendMessage("${stock.name} 주식 종목이 추가되었습니다.")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    then("delete") {
                        then("stock" to stock()) {
                            executes {
                                val stock: Stock by it
                                val stockName = stock.name
                                transaction {
                                    stock.delete()
                                }

                                sender.sendMessage("$stockName 주식 종목이 삭제되었습니다.")
                            }
                        }
                    }
                    then("update") {
                        then("stock" to stock()) {
                            then("amount" to int()) {
                                then("price" to int()) {
                                    then("fluctuation" to int()) {
                                        executes {
                                            val stock: Stock by it
                                            val amount: Int by it
                                            val price: Int by it
                                            val fluctuation: Int by it

                                            transaction {
                                                stock.apply {
                                                    this.remainingAmount = amount
                                                    this.currentPrice = price
                                                    this.fluctuation = fluctuation
                                                }
                                            }

                                            sender.sendMessage("${stock.name} 주식 종목이 수정되었습니다.")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                then("fluctuation") {
                    then("time" to int()) {
                        executes {
                            val time: Int by it

                            StockConfig.updateConfigData {
                                copy(fluctuationTime = time)
                            }

                            sender.sendMessage("주식 변동 시간이 $time 분으로 설정되었습니다.")
                        }
                    }
                }
            }
        }
    }
}

fun stock() = dynamic(type = StringType.QUOTABLE_PHRASE) { _, input ->
    transaction {
        Stock.find { Stocks.name eq input }.firstOrNull()
    }
}.apply {
    suggests {
        transaction {
            suggest(
                candidates = Stock.find { Stocks.name like "$it%" }.map { it.name },
                tooltip = { it.component() },
            )
        }
    }
}
