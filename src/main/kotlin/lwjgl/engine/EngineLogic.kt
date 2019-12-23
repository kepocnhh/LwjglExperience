package lwjgl.engine

import lwjgl.canvas.Canvas
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.KeyType

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
