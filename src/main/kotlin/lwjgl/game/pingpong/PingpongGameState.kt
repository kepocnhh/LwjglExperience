package lwjgl.game.pingpong

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
    }

    enum class Common {
        MAIN_MENU,
        COMPETITION
    }

    val common: Common
    val mainMenu: MainMenu
    val competition: Competition
}
