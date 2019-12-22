package lwjgl.game.pingpong

sealed class PingpongGameState {
    class MainMenu(
        val availableMenuItems: Set<MenuItem>,
        val selectedMenuItem: MenuItem
    ): PingpongGameState() {
        enum class MenuItem {
            START_NEW_GAME,
            CONTINUE_GAME,
            EXIT,
        }
    }
    class Game: PingpongGameState()
}