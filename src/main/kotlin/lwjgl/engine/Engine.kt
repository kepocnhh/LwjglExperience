package lwjgl.engine

import lwjgl.entity.Size
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.KeyType
import lwjgl.util.glfw.key.toKeyStatusOrNull
import lwjgl.util.glfw.key.toKeyTypeOrNull
import lwjgl.window.WindowSize
import lwjgl.window.loopWindow

object Engine {
    private const val nanoInSecond = 1_000_000_000L

    fun run(logic: EngineLogic) {
        val keysStatuses = mutableMapOf<KeyType, KeyStatus>().also { statuses ->
            KeyType.values().forEach { keyType ->
                statuses[keyType] = KeyStatus.RELEASE
            }
        }
        var timeLast = 0L
        val timeFrame = nanoInSecond.toDouble() / logic.framesPerSecondExpected // todo mutable fps?
        loopWindow(
            windowSize = WindowSize.Exact(size = Size(width = 640, height = 480)),
            title = "Engine",
            onKeyCallback = { windowId, key, _, action, _ ->
                val keyType = key.toKeyTypeOrNull()
                val keyStatus = action.toKeyStatusOrNull()
                if(keyType != null && keyStatus != null) {
                    keysStatuses[keyType] = keyStatus
                    println("key = $keyType, action = $keyStatus")
                    logic.onKeyCallback(
                        windowId,
                        keyType,
                        keyStatus
                    )
                }
            },
            onPreLoop = {
                //todo
            },
            onPostLoop = {
                //todo
            },
            onRender = { windowId, canvas ->
                while (System.nanoTime() - timeLast < timeFrame);
                val timeNow = System.nanoTime()
                logic.onRender(
                    windowId,
                    canvas,
                    keysStatuses,
                    renderProperty = EngineRenderProperty(timeLast = timeLast, timeNow = timeNow)
                )
                timeLast = System.nanoTime()
            }
        )
    }
}
