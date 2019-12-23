package lwjgl.game.pingpong

class MutablePingpongMainMenuState: PingpongGameState.MainMenu {
    override var selectedMenuItem = PingpongGameState.MainMenu.Item.START_NEW_GAME
}

class MutablePingpongCompetitionState: PingpongGameState.Competition {
    override var status = null
}

class MutablePingpongGameState: PingpongGameState {
    override var shouldEngineStop: Boolean = false
    private set
    fun engineStop() {
        shouldEngineStop = true
    }

    override var common: PingpongGameState.Common = PingpongGameState.Common.MAIN_MENU
    override val mainMenu = MutablePingpongMainMenuState()
    override val competition = MutablePingpongCompetitionState()
}
