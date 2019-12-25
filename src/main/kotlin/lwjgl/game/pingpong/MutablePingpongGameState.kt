package lwjgl.game.pingpong

import lwjgl.entity.Degrees
import lwjgl.entity.Percent
import lwjgl.entity.PointPercent

class MutablePingpongMainMenuState: PingpongGameState.MainMenu {
    override var selectedMenuItem = PingpongGameState.MainMenu.Item.START_NEW_GAME
}

class MutablePingpongCompetitionEnvironment(
    override var isPaused: Boolean,
    override var playerLeftYPercent: Percent,
    override var playerRightYPercent: Percent,
    override var ballCoordinate: PointPercent,
    override var ballDirection: Degrees,
    override var state: PingpongGameState.Competition.Environment.State
): PingpongGameState.Competition.Environment

class MutablePingpongCompetitionState: PingpongGameState.Competition {
    override var environment: MutablePingpongCompetitionEnvironment? = null
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
