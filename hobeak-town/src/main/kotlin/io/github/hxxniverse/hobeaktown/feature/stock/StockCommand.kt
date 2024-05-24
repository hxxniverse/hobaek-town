package io.github.hxxniverse.hobeaktown.feature.stock

import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stock
import io.github.hxxniverse.hobeaktown.feature.stock.entity.Stocks
import io.github.hxxniverse.hobeaktown.feature.stock.ui.StockStatusUi
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.monun.kommand.KommandArgument.Companion.dynamic
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * "명령어 리스트 ( Command list )
 * /주식
 *
 * /주식 종목 추가 (종목 이름) (판매개수) (가격) (변동폭 -> %로 변동)
 * ex Command ) /주식 종목 추가 후스텔라 100 5000000 30
 *
 * /주식 종목 삭제 (종목 이름)
 * ex Command ) /주식 종목 삭제 후스텔라
 *
 * /주식 종목 수정 (종목 이름) (판매개수) (가격) (변동폭 -> %로 변동)
 * ex Command ) /주식 종목 수정 후스텔라 200 8000000 20
 *
 * /주식 변동 (시간)
 * ex Command ) /주식 변동 60
 * ㄴ 60분 마다 변경됩니다."
 */
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
                        sender.sendMessage(
                            """
                            /stock item add (stockName) (amount) (price) (fluctuation)
                            /stock item delete (stockName)
                            /stock item update (stockName) (amount) (price) (fluctuation)
                            /stock fluctuation (time)
                            """.trimIndent(),
                        )
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
                            println(leftInterval)
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
                        then("stockName" to string()) {
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

fun stock() = dynamic { ctx, input ->
    transaction {
        Stock.find { Stocks.name eq input }.firstOrNull()
    }
}.apply {
    suggests {
        transaction {
            Stock.find { Stocks.name like "$it%" }.map { it.name }
        }
    }
}
