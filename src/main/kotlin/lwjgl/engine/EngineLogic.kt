package lwjgl.engine

import lwjgl.canvas.Canvas
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.KeyType

interface EngineLogic {
    val framesPerSecondExpected: Int

    fun onKeyCallback(windowId: Long, keyType: KeyType, keyStatus: KeyStatus)
    fun onRender(
        windowId: Long,
        canvas: Canvas,
        keysStatuses: Map<KeyType, KeyStatus>,
        renderProperty: EngineRenderProperty
    )
}
