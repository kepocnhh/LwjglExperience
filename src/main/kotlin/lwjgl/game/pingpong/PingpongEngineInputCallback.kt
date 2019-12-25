package lwjgl.game.pingpong

import lwjgl.engine.EngineInputCallback
import lwjgl.engine.EngineProperty
import lwjgl.engine.FunctionKey
import lwjgl.engine.PrintableKey
import lwjgl.entity.Percent
import lwjgl.entity.Point
import lwjgl.entity.PointPercent
import lwjgl.util.glfw.key.KeyStatus

class PingpongEngineInputCallback(
    private val mutableGameState: MutablePingpongGameState
): EngineInputCallback {
    override fun onPrintableKey(key: PrintableKey, status: KeyStatus) {
        val gameSate: PingpongGameState = mutableGameState
        when(key) {
            PrintableKey.S -> {
                when(status) {
                    KeyStatus.PRESS -> {
                        when(gameSate.common) {
                            PingpongGameState.Common.MAIN_MENU -> {
                                mutableGameState.mainMenu.selectedMenuItem =
                                    gameSate.getNextAvailableMenuItem()
                            }
                        }
                    }
                }
            }
            PrintableKey.W -> {
                when(status) {
                    KeyStatus.PRESS -> {
                        when(gameSate.common) {
                            PingpongGameState.Common.MAIN_MENU -> {
                                mutableGameState.mainMenu.selectedMenuItem =
                                    gameSate.getPreviousAvailableMenuItem()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun readyNewGame() {
        mutableGameState.competition.environment.ballCoordinate = PointPercent(0.5, 0.5)
        mutableGameState.competition.environment.playerLeftYPercent = Percent(0.5)
        mutableGameState.competition.environment.playerRightYPercent = Percent(0.5)
    }

    override fun onFunctionKey(key: FunctionKey, status: KeyStatus) {
        val gameSate: PingpongGameState = mutableGameState
        when(key) {
            FunctionKey.ENTER -> {
                when(status) {
                    KeyStatus.PRESS -> {
                        when(gameSate.common) {
                            PingpongGameState.Common.MAIN_MENU -> {
                                when(gameSate.mainMenu.selectedMenuItem) {
                                    PingpongGameState.MainMenu.Item.EXIT -> {
                                        mutableGameState.engineStop()
                                        return
                                    }
                                    PingpongGameState.MainMenu.Item.START_NEW_GAME -> {
                                        check(gameSate.competition.status == null)
                                        readyNewGame()
                                        mutableGameState.common = PingpongGameState.Common.COMPETITION
                                        return
                                    }
                                    PingpongGameState.MainMenu.Item.CONTINUE_GAME -> {
                                        check(gameSate.competition.status == PingpongGameState.Competition.Status.PAUSED)
                                        mutableGameState.competition.status = PingpongGameState.Competition.Status.STARTED
                                        mutableGameState.common = PingpongGameState.Common.COMPETITION
                                    }
                                }
                            }
                        }
                    }
                }
            }
            FunctionKey.ESCAPE -> {
                when (status) {
                    KeyStatus.PRESS -> {
                        when(gameSate.common) {
                            PingpongGameState.Common.MAIN_MENU -> {
                                when(gameSate.competition.status) {
                                    PingpongGameState.Competition.Status.STARTED -> error("not possible by status started")
                                    PingpongGameState.Competition.Status.PAUSED -> {
                                        mutableGameState.competition.status = PingpongGameState.Competition.Status.STARTED
                                        mutableGameState.common = PingpongGameState.Common.COMPETITION
                                        return
                                    }
                                }
                            }
                            PingpongGameState.Common.COMPETITION -> {
                                mutableGameState.competition.status = PingpongGameState.Competition.Status.PAUSED
                                mutableGameState.mainMenu.selectedMenuItem = PingpongGameState.MainMenu.Item.CONTINUE_GAME
                                mutableGameState.common = PingpongGameState.Common.MAIN_MENU
                                return
                            }
                        }
                    }
                }
            }
        }
    }
}
