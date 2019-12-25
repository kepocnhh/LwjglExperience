package lwjgl.engine

import lwjgl.entity.Size
import lwjgl.util.glfw.glfwGetWindowSize
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.toKeyStatusOrNull
import lwjgl.window.WindowSize
import lwjgl.window.closeWindow
import lwjgl.window.loopWindow
import org.lwjgl.glfw.GLFW
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
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

private fun getCurrentTimeFrame(timeLast: Long): Long {
    return System.nanoTime() - timeLast
}
private fun syncTimeFrame(timeLast: Long, timeFrame: Double) {
    while (true) {
        val dif = getCurrentTimeFrame(timeLast)
        if(dif >= timeFrame) return
        val timeFrameHalf = timeFrame / 2
        if(dif < timeFrameHalf) {
            Thread.sleep(timeFrameHalf.toLong() / 1_000_000)
        }
    }
}

object Engine {
    const val nanoInSecond = 1_000_000_000L

    fun run(logic: EngineLogic) {
        val isWindowClosed = AtomicBoolean(false)
        val mutableEngineInputState = MutableEngineInputState()
//        val isRenderAsync = true
        val isRenderAsync = false
//        val isFixFrameRate = true
        val isFixFrameRate = false

        var timeRenderLast = 0L
        var timeLogicLast = 0L
        val timeRenderFrame = nanoInSecond.toDouble() / logic.framesPerSecondExpected // todo mutable fps?
        val callbackQueue: BlockingQueue<() -> Unit> = LinkedBlockingQueue()
        loopWindow(
            windowSize = WindowSize.Exact(size = Size(width = 640, height = 480)),
//            windowSize = WindowSize.FullScreen,
            title = "Engine",
            onKeyCallback = { _, key, _, action, _ ->
                val keyStatus = action.toKeyStatusOrNull()
                if(keyStatus != null && keyStatus != KeyStatus.REPEAT) {
                    val printableKey = key.toPrintableKeyOrNull()
                    if(printableKey != null) {
                        println("printable key = $printableKey, action = $keyStatus")
                        mutableEngineInputState.keyboard.printableKeys[printableKey] = keyStatus
                        if(isRenderAsync) {
                            callbackQueue.offer {
                                logic.engineInputCallback.onPrintableKey(printableKey, keyStatus)
                            }
                        } else {
                            logic.engineInputCallback.onPrintableKey(printableKey, keyStatus)
                        }
                    } else {
                        val functionKey = key.toFunctionKeyOrNull()
                        if(functionKey != null) {
                            println("function key = $functionKey, action = $keyStatus")
                            mutableEngineInputState.keyboard.functionKeys[functionKey] = keyStatus
                            if(isRenderAsync) {
                                callbackQueue.offer {
                                    logic.engineInputCallback.onFunctionKey(functionKey, keyStatus)
                                }
                            } else {
                                logic.engineInputCallback.onFunctionKey(functionKey, keyStatus)
                            }
                        }
                    }
                }
            },
            onWindowCloseCallback = {
                isWindowClosed.set(true)
            },
            onPreLoop = { windowId ->
                if(!isRenderAsync) return@loopWindow
                fun shouldEngineStop(): Boolean {
                    return logic.shouldEngineStop || GLFW.glfwWindowShouldClose(windowId) || isWindowClosed.get()
                }

                val timeLogicFrame = nanoInSecond.toDouble() / 120

                var timeInputLast = System.nanoTime()
                thread {
                    println("input loop started")
                    while (!shouldEngineStop()) {
                        if(isFixFrameRate) syncTimeFrame(timeLast = timeInputLast, timeFrame = timeLogicFrame)
                        while (true) {
                            val action = callbackQueue.poll() ?: break
                            action()
                        }
                        timeInputLast = System.nanoTime()
                    }
                    println("input loop finished")
                }

                thread {
                    println("logic loop started")
                    while (!shouldEngineStop()) {
                        if(isFixFrameRate) syncTimeFrame(timeLast = timeLogicLast, timeFrame = timeLogicFrame)
                        val timeNow = System.nanoTime()
                        logic.onUpdateState(
                            engineInputState = mutableEngineInputState,
                            engineProperty = EngineProperty(
                                timeLast = timeLogicLast,
                                timeNow = timeNow,
                                pictureSize = glfwGetWindowSize(windowId)
                            )
                        )
                        timeLogicLast = timeNow
                    }
                    println("logic loop finished")
                }
            },
            onPostLoop = {
                //todo
            },
            onRender = { windowId, canvas ->
                if(!isRenderAsync) {
                    val timeNow = System.nanoTime()
                    logic.onUpdateState(
                        engineInputState = mutableEngineInputState,
                        engineProperty = EngineProperty(
                            timeLast = timeLogicLast,
                            timeNow = timeNow,
                            pictureSize = glfwGetWindowSize(windowId)
                        )
                    )
                    timeLogicLast = timeNow
                }
                if(isFixFrameRate) syncTimeFrame(timeLast = timeRenderLast, timeFrame = timeRenderFrame)
                val timeNow = System.nanoTime()
                logic.onRender(
                    canvas,
                    engineInputState = mutableEngineInputState,
                    engineProperty = EngineProperty(
                        timeLast = timeRenderLast,
                        timeNow = timeNow,
                        pictureSize = glfwGetWindowSize(windowId)
                    )
                )
                timeRenderLast = timeNow
                if(logic.shouldEngineStop) {
                    closeWindow(windowId)
                }
            }
        )
    }
}
