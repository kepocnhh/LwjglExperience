package lwjgl.engine

import lwjgl.entity.Size
import lwjgl.util.glfw.glfwGetWindowSize
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.toKeyStatusOrNull
import lwjgl.window.WindowSize
import lwjgl.window.closeWindow
import lwjgl.window.loopWindow
import org.lwjgl.glfw.GLFW
import kotlin.concurrent.thread

private class MutableEngineInputKeyboardState: EngineInputState.Keyboard {
    override val printableKeys: MutableMap<PrintableKey, KeyStatus> = mutableMapOf<PrintableKey, KeyStatus>().also { statuses ->
        PrintableKey.values().forEach { keyType ->
            statuses[keyType] = KeyStatus.RELEASE
        }
    }
    override val functionKeys: MutableMap<FunctionKey, KeyStatus> = mutableMapOf<FunctionKey, KeyStatus>().also { statuses ->
        FunctionKey.values().forEach { keyType ->
            statuses[keyType] = KeyStatus.RELEASE
        }
    }
}
private class MutableEngineInputState: EngineInputState {
    override val keyboard: MutableEngineInputKeyboardState = MutableEngineInputKeyboardState()
}

object Engine {
    private const val nanoInSecond = 1_000_000_000L

    fun run(logic: EngineLogic) {
        val mutableEngineInputState = MutableEngineInputState()

        var timeRenderLast = 0L
        val timeRenderFrame = nanoInSecond.toDouble() / logic.framesPerSecondExpected // todo mutable fps?
        loopWindow(
            windowSize = WindowSize.Exact(size = Size(width = 640, height = 480)),
//            windowSize = WindowSize.FullScreen,
            title = "Engine",
            onKeyCallback = { _, key, _, action, _ ->
                val keyStatus = action.toKeyStatusOrNull()
                if(keyStatus != null) {
                    val printableKey = key.toPrintableKeyOrNull()
                    if(printableKey != null) {
                        println("printable key = $printableKey, action = $keyStatus")
                        mutableEngineInputState.keyboard.printableKeys[printableKey] = keyStatus
                        logic.engineInputCallback.onPrintableKey(printableKey, keyStatus)
                    } else {
                        val functionKey = key.toFunctionKeyOrNull()
                        if(functionKey != null) {
                            println("function key = $functionKey, action = $keyStatus")
                            mutableEngineInputState.keyboard.functionKeys[functionKey] = keyStatus
                            logic.engineInputCallback.onFunctionKey(functionKey, keyStatus)
                        }
                    }
                }
            },
            onPreLoop = { windowId ->
                var timeLogicLast = 0L
                val timeLogicFrame = nanoInSecond.toDouble() / 25
                thread {
                    while (!logic.shouldEngineStop && !GLFW.glfwWindowShouldClose(windowId)) {
                        while (true) {
                            val dif = System.nanoTime() - timeLogicLast
                            if(dif > timeLogicFrame) break
                            val timeFrameHalf = timeLogicFrame / 2
                            if(dif < timeFrameHalf) {
                                Thread.sleep(timeFrameHalf.toLong() / 1_000_000)
                            }
                        }
                        logic.onUpdateState(
                            engineInputState = mutableEngineInputState,
                            engineProperty = EngineProperty(
                                timeLast = timeLogicLast,
                                timeNow = System.nanoTime(),
                                pictureSize = glfwGetWindowSize(windowId)
                            )
                        )
                        timeLogicLast = System.nanoTime()
                    }
                }
            },
            onPostLoop = {
                //todo
            },
            onRender = { windowId, canvas ->
                while (true) {
                    val dif = System.nanoTime() - timeRenderLast
                    if(dif > timeRenderFrame) break
                    val timeFrameHalf = timeRenderFrame / 2
                    if(dif < timeFrameHalf) {
                        Thread.sleep(timeFrameHalf.toLong() / 1_000_000)
                    }
                }
//                logic.onUpdateState(
//                    engineInputState = mutableEngineInputState,
//                    engineProperty = EngineProperty(timeLast = timeRenderLast, timeNow = System.nanoTime())
//                )
                logic.onRender(
                    canvas,
                    engineInputState = mutableEngineInputState,
                    engineProperty = EngineProperty(
                        timeLast = timeRenderLast,
                        timeNow = System.nanoTime(),
                        pictureSize = glfwGetWindowSize(windowId)
                    )
                )
                timeRenderLast = System.nanoTime()
                if(logic.shouldEngineStop) {
                    closeWindow(windowId)
                }
            }
        )
    }
}
