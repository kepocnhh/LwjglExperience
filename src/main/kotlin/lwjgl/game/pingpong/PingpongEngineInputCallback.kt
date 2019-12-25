package lwjgl.game.pingpong

import lwjgl.engine.EngineInputCallback
import lwjgl.engine.FunctionKey
import lwjgl.engine.PrintableKey
import lwjgl.entity.Degrees
import lwjgl.entity.Percent
import lwjgl.entity.PointPercent
import lwjgl.util.glfw.key.KeyStatus
import kotlin.random.Random

class PingpongEngineInputCallback(
    private val mutableGameState: MutablePingpongGameState,
    private val mutablePingpongGameSettings: MutablePingpongGameSettings
): EngineInputCallback {
    override fun onPrintableKey(key: PrintableKey, status: KeyStatus) {
        val gameState: PingpongGameState = mutableGameState
        when(key) {
            PrintableKey.S -> {
                when(status) {
                    KeyStatus.PRESS -> {
                        when(gameState.common) {
                            PingpongGameState.Common.MAIN_MENU -> {
                                mutableGameState.mainMenu.selectedMenuItem =
                                    gameState.getNextAvailableMenuItem()
                            }
                        }
                    }
                }
            }
            PrintableKey.W -> {
                when(status) {
                    KeyStatus.PRESS -> {
                        when(gameState.common) {
                            PingpongGameState.Common.MAIN_MENU -> {
                                mutableGameState.mainMenu.selectedMenuItem =
                                    gameState.getPreviousAvailableMenuItem()
                            }
                        }
                    }
                }
            }
            PrintableKey.U -> {
                when(status) {
                    KeyStatus.PRESS -> {
                        mutablePingpongGameSettings.isDebugEnabled = !mutablePingpongGameSettings.isDebugEnabled
                    }
                }
            }
        }
    }

    private fun readyNewGame() {
        mutableGameState.competition.environment = MutablePingpongCompetitionEnvironment(
            isPaused = false,
            ballCoordinate = PointPercent(0.5, 0.5),
            ballDirection = Degrees(0.0),
            playerLeftYPercent = Percent(0.5),
            playerRightYPercent = Percent(0.5),
            state = PingpongGameState.Competition.Environment.State.PITCH
        )
    }

    override fun onFunctionKey(key: FunctionKey, status: KeyStatus) {
        val gameState: PingpongGameState = mutableGameState
        when(key) {
            FunctionKey.ENTER -> {
                when(status) {
                    KeyStatus.PRESS -> {
                        when(gameState.common) {
                            PingpongGameState.Common.MAIN_MENU -> {
                                when(gameState.mainMenu.selectedMenuItem) {
                                    PingpongGameState.MainMenu.Item.EXIT -> {
                                        mutableGameState.engineStop()
                                        return
                                    }
                                    PingpongGameState.MainMenu.Item.START_NEW_GAME -> {
                                        val environment = mutableGameState.competition.environment
                                        check(environment == null)
                                        readyNewGame()
                                        mutableGameState.common = PingpongGameState.Common.COMPETITION
                                        return
                                    }
                                    PingpongGameState.MainMenu.Item.CONTINUE_GAME -> {
                                        val environment = mutableGameState.competition.environment
                                        checkNotNull(environment)
                                        check(environment.isPaused)
                                        environment.isPaused = false
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
                        when(gameState.common) {
                            PingpongGameState.Common.MAIN_MENU -> {
                                val environment = mutableGameState.competition.environment
                                when {
                                    environment == null -> {
                                        //todo
                                    }
                                    environment.isPaused -> {
                                        environment.isPaused = false
                                        mutableGameState.common = PingpongGameState.Common.COMPETITION
                                        return
                                    }
                                    else -> error("not possible if environment is not paused")
                                }
                            }
                            PingpongGameState.Common.COMPETITION -> {
                                val environment = mutableGameState.competition.environment
                                checkNotNull(environment)
                                check(!environment.isPaused)
                                environment.isPaused = true
                                mutableGameState.mainMenu.selectedMenuItem = mutableGameState.getAvailableMenuItems().first()
                                mutableGameState.common = PingpongGameState.Common.MAIN_MENU
                                return
                            }
                        }
                    }
                }
            }
            FunctionKey.SPACE -> {
                when (status) {
                    KeyStatus.PRESS -> {
                        when(gameState.common) {
                            PingpongGameState.Common.COMPETITION -> {
                                val environment = mutableGameState.competition.environment
                                checkNotNull(environment)
                                when(environment.state) {
                                    PingpongGameState.Competition.Environment.State.PITCH -> {
                                        val degreesValue = Random.nextDouble(from = -Degrees.maxDegreesValue/8, until = Degrees.maxDegreesValue/8)
                                        environment.ballDirection = Degrees(degreesValue)
                                        environment.ballCoordinate = PointPercent(
                                            xPercent = 0.0,
                                            yPercent = environment.playerLeftYPercent.value
                                        )
                                        environment.state = PingpongGameState.Competition.Environment.State.GAME
                                        return
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
