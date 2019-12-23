package lwjgl.game.pingpong

import lwjgl.canvas.Canvas
import lwjgl.engine.EngineInputState
import lwjgl.engine.EngineLogic
import lwjgl.engine.EngineProperty
import lwjgl.entity.Color
import lwjgl.entity.Point
import lwjgl.game.pingpong.render.PingpongMainMenuRender
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
        //todo
    }

    private val mainMenuRender = PingpongMainMenuRender()
    override fun onRender(
        canvas: Canvas,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {
        val gameSate: PingpongGameState = mutableGameState
        val gameSettings: PingpongGameSettings = mutableGameSettings
        when(gameSate.common) {
            PingpongGameState.Common.MAIN_MENU -> {
                mainMenuRender.onRender(
                    canvas, gameSate, gameSettings, engineInputState, engineProperty
                )
            }
            PingpongGameState.Common.COMPETITION -> TODO()
        }
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
