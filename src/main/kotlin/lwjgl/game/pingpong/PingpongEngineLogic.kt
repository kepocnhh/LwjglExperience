package lwjgl.game.pingpong

import lwjgl.canvas.Canvas
import lwjgl.engine.*
import lwjgl.entity.Color
import lwjgl.entity.Percent
import lwjgl.entity.Point
import lwjgl.game.pingpong.render.PingpongCompetitionRender
import lwjgl.game.pingpong.render.PingpongMainMenuRender
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.resource.ResourceProvider

object PingpongEngineLogic: EngineLogic {
    private val mutableGameState = MutablePingpongGameState()
    private val mutableGameSettings = MutablePingpongGameSettings(
        fullPathFontMain = ResourceProvider.getResourceAsFile("font.main.ttf").absolutePath,
        defaultColor = Color.GREEN,
        defaultFontHeight = 16f
    )

    override val framesPerSecondExpected: Int = 60
    override val shouldEngineStop: Boolean get() {
        return mutableGameState.shouldEngineStop
    }

    override val engineInputCallback = PingpongEngineInputCallback(mutableGameState)

    override fun onUpdateState(
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {
        val gameSate: PingpongGameState = mutableGameState
        when(gameSate.common) {
            PingpongGameState.Common.MAIN_MENU -> {
                //todo
            }
            PingpongGameState.Common.COMPETITION -> {
                when(engineInputState.keyboard.printableKeys[PrintableKey.W]) {
                    KeyStatus.PRESS -> {
                        if(engineInputState.keyboard.printableKeys[PrintableKey.S] == KeyStatus.PRESS) return
                        if(mutableGameState.competition.environment.playerLeftYPercent.value == 0.0) return
                        val playerRacketAcceleration = mutableGameSettings.playerRacketPercentPerSecond.value / Engine.nanoInSecond
                        val timeDifference = engineProperty.timeNow - engineProperty.timeLast
                        val delta = timeDifference * playerRacketAcceleration
                        val oldValue = mutableGameState.competition.environment.playerLeftYPercent.value
                        val newValue = oldValue - delta
                        mutableGameState.competition.environment.playerLeftYPercent = Percent(if(newValue < 0.0) 0.0 else newValue)
                    }
                }
                when(engineInputState.keyboard.printableKeys[PrintableKey.S]) {
                    KeyStatus.PRESS -> {
                        if(engineInputState.keyboard.printableKeys[PrintableKey.W] == KeyStatus.PRESS) return
                        if(mutableGameState.competition.environment.playerLeftYPercent.value == 1.0) return
                        val playerRacketAcceleration = mutableGameSettings.playerRacketPercentPerSecond.value / Engine.nanoInSecond
                        val timeDifference = engineProperty.timeNow - engineProperty.timeLast
                        val delta = timeDifference * playerRacketAcceleration
                        val oldValue = mutableGameState.competition.environment.playerLeftYPercent.value
                        val newValue = oldValue + delta
                        mutableGameState.competition.environment.playerLeftYPercent = Percent(if(newValue > 1.0) 1.0 else newValue)
                    }
                }
            }
        }
    }

    private val mainMenuRender = PingpongMainMenuRender()
    private val competitionRender = PingpongCompetitionRender()
    override fun onRender(
        canvas: Canvas,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {
        val gameSate: PingpongGameState = mutableGameState
        val gameSettings: PingpongGameSettings = mutableGameSettings
        val render = when(gameSate.common) {
            PingpongGameState.Common.MAIN_MENU -> mainMenuRender
            PingpongGameState.Common.COMPETITION -> competitionRender
        }
        render.onRender(
            canvas, gameSate, gameSettings, engineInputState, engineProperty
        )
    }
}

fun Canvas.drawText(
    gameSettings: PingpongGameSettings,
    pointTopLeft: Point,
    text: CharSequence
) {
    drawText(
        fullPathFont = gameSettings.fullPathFontMain,
        color = gameSettings.defaultColor,
        pointTopLeft = pointTopLeft,
        text = text,
        fontHeight = gameSettings.defaultFontHeight
    )
}
