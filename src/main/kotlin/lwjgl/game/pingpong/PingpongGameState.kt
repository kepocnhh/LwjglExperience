package lwjgl.game.pingpong

import lwjgl.entity.Percent
import lwjgl.entity.PointPercent

interface PingpongGameState {
    val shouldEngineStop: Boolean

    interface MainMenu {
        enum class Item {
            START_NEW_GAME,
            CONTINUE_GAME,
            EXIT,
        }

        val selectedMenuItem: Item
    }

    interface Competition {
        enum class Status {
            STARTED,
            PAUSED,
        }

        val status: Status?

        interface Environment {
            val playerLeftYPercent: Percent
            val playerRightYPercent: Percent
            val ballCoordinate: PointPercent
        }

        val environment: Environment
    }

    enum class Common {
        MAIN_MENU,
        COMPETITION
    }

    val common: Common
    val mainMenu: MainMenu
    val competition: Competition
}
