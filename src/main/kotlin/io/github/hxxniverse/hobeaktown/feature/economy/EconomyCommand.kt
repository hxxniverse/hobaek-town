package io.github.hxxniverse.hobeaktown.feature.economy

import io.github.hxxniverse.hobeaktown.feature.economy.entity.Atm
import io.github.hxxniverse.hobeaktown.feature.economy.ui.AtmDepositUi
import io.github.hxxniverse.hobeaktown.feature.economy.ui.AtmMenuUi
import io.github.hxxniverse.hobeaktown.feature.economy.ui.AtmRemittanceRecipientUi
import io.github.hxxniverse.hobeaktown.feature.economy.ui.AtmWithdrawUi
import io.github.hxxniverse.hobeaktown.feature.economy.util.*
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import io.github.monun.kommand.node.KommandNode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.DecimalFormat
import java.util.*


class EconomyCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("atm") {
                requires { sender.isOp }
                then("help") {
                    executes {
                        text("ATM")
                            .also(sender::sendMessage)
                        text("* /atm open - ATM을 엽니다.")
                            .also(sender::sendMessage)
                        text("* /atm remittance - 송금을 합니다.")
                            .also(sender::sendMessage)
                        text("* /atm deposit - 입금을 합니다.")
                            .also(sender::sendMessage)
                        text("* /atm withdraw - 출금을 합니다.")
                            .also(sender::sendMessage)
                        text("* /atm check [player] - 플레이어의 금액을 확인합니다.")
                            .also(sender::sendMessage)
                        text("* /atm block - ATM 블록을 설정합니다.")
                            .also(sender::sendMessage)
                        text("* /atm set [player] [money] - 플레이어의 금액을 설정합니다.")
                            .also(sender::sendMessage)
                        text("* /atm give [player] [money] - 플레이어에게 금액을 지급합니다.")
                            .also(sender::sendMessage)
                        text("* /atm take [player] [money] - 플레이어의 금액을 차감합니다.")
                            .also(sender::sendMessage)
                    }
                }
                then("open") {
                    requires { sender is Player }
                    executes {
                        AtmMenuUi().open(player)
                    }
                }
                then("remittance") {
                    requires { sender is Player }
                    executes {
                        AtmRemittanceRecipientUi().open(player)
                    }
                }
                then("deposit") {
                    requires { sender is Player }
                    executes {
                        AtmDepositUi().open(player)
                    }
                }
                then("withdraw") {
                    requires { sender is Player }
                    executes {
                        AtmWithdrawUi().open(player)
                    }
                }
                then("check") {
                    then("player" to player()) {
                        executes {
                            val player: Player by it

                            text(player.name)
                                .append(text("님의 금액: "))
                                .append(text(DecimalFormat("#,##0").format(player.money)))
                                .also(sender::sendMessage)

                            text(player.name)
                                .append(text("님의 코인: "))
                                .append(text(DecimalFormat("#,##0").format(player.cash)))
                                .also(sender::sendMessage)
                        }
                    }
                }
                then("block") {
                    requires { sender is Player }
                    executes {
                        val block = player.getTargetBlock(null, 5)

                        if (block.type == Material.AIR) {
                            text("블록을 찾을 수 없습니다.")
                                .also(sender::sendMessage)
                            return@executes
                        }

                        transaction {
                            val atm = Atm.findByLocation(block.location)

                            if (atm != null) {
                                atm.delete()
                                text("ATM 블록에서 제거되었습니다.")
                                    .also(sender::sendMessage)
                                return@transaction
                            } else {
                                Atm.create(block.location)
                                text("ATM 블록으로 설정되었습니다.")
                                    .also(sender::sendMessage)
                            }
                        }
                    }
                }
                then("set") {
                    then("player" to player()) {
                        then("currency" to dynamicByEnum(EnumSet.allOf(Currency::class.java))) {
                            then("money" to int()) {
                                executes {
                                    val player: Player by it
                                    val currency: Currency by it
                                    val money: Int by it

                                    when (currency) {
                                        Currency.MONEY -> player.money = money
                                        Currency.CASH -> player.cash = money
                                    }

                                    text(player.name)
                                        .append(text("님의 ${currency.symbol}을(를) "))
                                        .append(text(DecimalFormat("#,##0").format(money)))
                                        .append(text("로 변경되었습니다."))
                                        .also(sender::sendMessage)
                                }
                            }
                        }
                    }
                }
                then("give") {
                    then("player" to player()) {
                        then("currency" to dynamicByEnum(EnumSet.allOf(Currency::class.java))) {
                            then("money" to int()) {
                                executes {
                                    val player: Player by it
                                    val currency: Currency by it
                                    val money: Int by it

                                    when (currency) {
                                        Currency.MONEY -> player.money += money
                                        Currency.CASH -> player.cash += money
                                    }

                                    text(player.name)
                                        .append(text("님의 ${currency.symbol}을(를) "))
                                        .append(text(DecimalFormat("#,##0").format(money)))
                                        .append(text("만큼 지급시켰습니다."))
                                        .also(sender::sendMessage)
                                }
                            }
                        }
                    }
                }
                then("take") {
                    then("player" to player()) {
                        then("currency" to currency()) {
                            then("money" to int()) {
                                executes {
                                    val player: Player by it
                                    val currency: Currency by it
                                    val money: Int by it

                                    when (currency) {
                                        Currency.MONEY -> player.money -= money
                                        Currency.CASH -> player.cash -= money
                                    }

                                    text(player.name)
                                        .append(text("님의 ${currency.symbol}을(를) "))
                                        .append(text(DecimalFormat("#,##0").format(money)))
                                        .append(text("만큼 차감시켰습니다."))
                                        .also(sender::sendMessage)
                                }
                            }
                        }
                    }
                }
                then("set-item") {
                    then("currency" to dynamicByEnum(EnumSet.allOf(Currency::class.java))) {
                        then("money" to dynamicByEnum(EnumSet.allOf(AmountUnit::class.java))) {
                            executes {
                                val currency: Currency by it
                                val money: AmountUnit by it

                                if (currency == Currency.MONEY) {
                                    player.inventory.setItemInMainHand(
                                        money.value.toPaperMoney(player.inventory.itemInMainHand.type)
                                    )
                                } else {
                                    player.inventory.setItemInMainHand(
                                        money.value.toCashCoin(player.inventory.itemInMainHand.type)
                                    )
                                }

                                text("아이템을 설정하였습니다.")
                                    .also(sender::sendMessage)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun KommandNode.currency() = dynamicByEnum(
        EnumSet.allOf(Currency::class.java),
        tooltip = {
            it.name.text()
        },
    )
}

enum class Currency(
    val symbol: String,
) {
    MONEY("돈"), CASH("코인")
}

enum class AmountUnit(
    val value: Int
) {
    _500(500), _1000(1000), _10000(10000), _100000(100000), _1000000(1000000), _10000000(10000000), _100000000(100000000)
}