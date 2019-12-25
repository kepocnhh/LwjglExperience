package lwjgl.game.pingpong

import lwjgl.entity.Percent
import lwjgl.entity.PointPercent

class MutablePingpongMainMenuState: PingpongGameState.MainMenu {
    override var selectedMenuItem = PingpongGameState.MainMenu.Item.START_NEW_GAME
}

class MutablePingpongCompetitionEnvironment: PingpongGameState.Competition.Environment {
    override var playerLeftYPercent = Percent(0.0)
    override var playerRightYPercent = Percent(0.0)
    override var ballCoordinate = PointPercent(0.0, 0.0)
}
class MutablePingpongCompetitionState: PingpongGameState.Competition {
    override var status: PingpongGameState.Competition.Status? = null
    override val environment: MutablePingpongCompetitionEnvironment = MutablePingpongCompetitionEnvironment()
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
