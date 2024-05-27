package io.github.hxxniverse.hobeaktown.feature.economy

import io.github.hxxniverse.hobeaktown.feature.economy.ui.*
import io.github.hxxniverse.hobeaktown.feature.economy.util.money
import io.github.hxxniverse.hobeaktown.util.base.BaseCommand
import io.github.hxxniverse.hobeaktown.util.extension.setPersistentData
import io.github.hxxniverse.hobeaktown.util.extension.text
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.text.DecimalFormat


/**
 * "명령어 리스트 ( Command list )
 * 모든 명령어는 OP만 사용가능합니다.
 * /atm remittance
 * 명령어 입력시 GUI가 출력됩니다.
 *
 * /atm deposit
 * 명령어 입력시 GUI가 출력됩니다.
 *
 * /atm withdraw
 * 명령어 입력시 GUI가 출력됩니다.
 *
 * /atm check (플레이어)
 * 플레이어가 소지하고있는 금액및 정보를 확인 할 수있습니다.
 *
 * /atm block
 * 타겟 엔티티의 정보에 isAtmBlock을 true로 변경합니다.
 *
 * /atm set (플레이어) (금액)
 * 플레이어의 ATM 금액을 변경합니다.
 *
 * /atm give (플레이어) (금액)
 * 플레이어의 ATM 금액에서 지정한 금액을 추가합니다.
 *
 * /atm take (플레이어) (금액)
 * 플레이어의 ATM 금액에서 지정한 금액을 차감합니다."
 *
 */
class EconomyCommand : BaseCommand {
    override fun register(plugin: JavaPlugin) {
        plugin.kommand {
            register("atm") {
                requires { sender.isOp }
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
                        }
                    }
                }
                then("block") {
                    requires { sender is Player }
                    executes {
                        val entity = player.getTargetEntity(100)

                        entity?.setPersistentData("isAtmBlock", true)

                        text("타겟 엔티티의 정보에 isAtmBlock을 true로 변경하였습니다.")
                    }
                }
                then("set") {
                    then("player" to player()) {
                        then("money" to int()) {
                            executes {
                                val player: Player by it
                                val money: Int by it

                                player.money = money

                                text(player.name)
                                    .append(text("님의 금액을 "))
                                    .append(text(DecimalFormat("#,##0").format(money)))
                                    .append(text("로 변경되었습니다."))
                                    .also(sender::sendMessage)
                            }
                        }
                    }
                }
                then("give") {
                    then("player" to player()) {
                        then("money" to int()) {
                            executes {
                                val player: Player by it
                                val money: Int by it

                                player.money += money

                                text(player.name)
                                    .append(text("님의 금액을 "))
                                    .append(text(DecimalFormat("#,##0").format(money)))
                                    .append(text("만큼 증가시켰습니다."))
                                    .also(sender::sendMessage)
                            }
                        }
                    }
                }
                then("take") {
                    then("player" to player()) {
                        then("money" to int()) {
                            executes {
                                val player: Player by it
                                val money: Int by it

                                player.money -= money

                                text(player.name)
                                    .append(text("님의 금액을 "))
                                    .append(text(DecimalFormat("#,##0").format(money)))
                                    .append(text("만큼 차감시켰습니다."))
                                    .also(sender::sendMessage)
                            }
                        }
                    }
                }
            }
        }
    }
}