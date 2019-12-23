package lwjgl.engine

import lwjgl.canvas.Canvas

interface EngineLogic {
    val framesPerSecondExpected: Int
    val shouldEngineStop: Boolean
    val engineInputCallback: EngineInputCallback

    fun onUpdateState(
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    )
    fun onRender(
        canvas: Canvas,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    )
}
