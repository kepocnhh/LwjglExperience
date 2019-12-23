package lwjgl.game.pingpong.render

import lwjgl.canvas.Canvas
import lwjgl.engine.EngineInputState
import lwjgl.engine.EngineProperty
import lwjgl.game.pingpong.PingpongGameSettings
import lwjgl.game.pingpong.PingpongGameState

interface PingpongRender {
    fun onRender(
        canvas: Canvas,
        gameState: PingpongGameState,
        gameSettings: PingpongGameSettings,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    )
}
