package lwjgl.game.pingpong

import lwjgl.engine.EngineInputCallback
import lwjgl.engine.FunctionKey
import lwjgl.engine.PrintableKey
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
