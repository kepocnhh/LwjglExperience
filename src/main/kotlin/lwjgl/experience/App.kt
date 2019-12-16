package lwjgl.experience

import lwjgl.canvas.Canvas
import lwjgl.entity.*
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.KeyType
import lwjgl.util.glfw.key.toKeyStatusOrNull
import lwjgl.util.glfw.key.toKeyTypeOrNull
import lwjgl.util.glfw.opengl.glClearColor
import lwjgl.util.resource.ResourceProvider
import lwjgl.window.closeWindow
import lwjgl.window.loopWindow
import org.lwjgl.Version
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
private val keysStatuses = mutableMapOf<KeyType, KeyStatus>().also { statuses ->
    KeyType.values().forEach { keyType ->
        statuses[keyType] = KeyStatus.RELEASE
    }
}
private var timeLast = 0L
private const val nanoInSecond = 1_000_000_000L
private const val framesPerSecond = 60L
private const val gameObjectPxPerSecond = 100
private const val gameObjectAcceleration = gameObjectPxPerSecond.toDouble() / nanoInSecond
private const val timeFrame = nanoInSecond.toDouble() / framesPerSecond
private val isWindowShouldClose = AtomicBoolean(false)
fun main() {
    println("Hello LWJGL " + Version.getVersion() + "!")
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        throwable.apply {
            println(
                javaClass.name + " " + message + buildString {
                    stackTrace.forEach { append("\n\t$it") }
                }
            )
        }
        defaultExceptionHandler.uncaughtException(thread, throwable)
    }
    thread {
        while (!isWindowShouldClose.get()) {
            val action = backgroundActions.poll()
            if(action != null) {
                thread {
                    action()
                }
            }
        }
    }
    loopWindow(
        width = 640,
        height = 480,
        title = "lwjgl.experience",
        onKeyCallback = { windowId, key, _, action, _ ->
            val keyType = key.toKeyTypeOrNull()
            val keyStatus = action.toKeyStatusOrNull()
            if(keyType != null && keyStatus != null) {
                keysStatuses[keyType] = keyStatus
                onKeyCallback(
                    windowId,
                    keyType,
                    keyStatus
                )
            }
        },
        onPreLoop = {
            glClearColor(Color.BLACK)
            println("""
                gameObjectAcceleration: $gameObjectAcceleration
                timeFrame: $timeFrame
            """.trimIndent())
            timeLast = System.nanoTime()
        },
        onPostLoop = {
            isWindowShouldClose.set(true)
        },
        onRender = ::onRender
    )
}

private class GameObject(
    var position: Point
)
private val gameObject = GameObject(position = Point(x = 0, y = 0))

private val backgroundActions: Queue<() -> Unit> = LinkedBlockingQueue()
fun postBackground(action: () -> Unit) {
    backgroundActions.add(action)
}
private fun moveGameObject(mapper: (Point, Double) -> Point) {
    val position = gameObject.position
    val timeLastInternal = timeLast
    while (timeLastInternal == timeLast) {
        val timeDifference = System.nanoTime() - timeLastInternal
        val delta = timeDifference * gameObjectAcceleration
        gameObject.position = mapper(position, delta)
    }
}
private fun onKeyCallback(windowId: Long, keyType: KeyType, keyStatus: KeyStatus) {
    println("key = $keyType, action = $keyStatus")
    when(keyType) {
        KeyType.ESCAPE -> {
            when(keyStatus) {
                KeyStatus.RELEASE -> {
                    closeWindow(windowId)
                    isWindowShouldClose.set(true)
                }
                else -> Unit//ignored
            }
        }
        KeyType.A -> {
            when(keyStatus) {
                KeyStatus.PRESS -> {
                    postBackground {
                        while (keysStatuses[keyType] != KeyStatus.RELEASE) {
                            if(keysStatuses[KeyType.D] != KeyStatus.RELEASE) continue
                            moveGameObject { oldPosition, delta ->
                                Point(
                                    x = oldPosition.x - delta.toFloat(),
                                    y = gameObject.position.y
                                )
                            }
                        }
                    }
                }
            }
        }
        KeyType.D -> {
            when(keyStatus) {
                KeyStatus.PRESS -> {
                    postBackground {
                        while (keysStatuses[keyType] != KeyStatus.RELEASE) {
                            if(keysStatuses[KeyType.A] != KeyStatus.RELEASE) continue
                            moveGameObject { oldPosition, delta ->
                                Point(
                                    x = oldPosition.x + delta.toFloat(),
                                    y = gameObject.position.y
                                )
                            }
                        }
                    }
                }
            }
        }
        KeyType.S -> {
            when(keyStatus) {
                KeyStatus.PRESS -> {
                    postBackground {
                        while (keysStatuses[keyType] != KeyStatus.RELEASE) {
                            if(keysStatuses[KeyType.W] != KeyStatus.RELEASE) continue
                            moveGameObject { oldPosition, delta ->
                                Point(
                                    x = gameObject.position.x,
                                    y = oldPosition.y + delta.toFloat()
                                )
                            }
                        }
                    }
                }
            }
        }
        KeyType.W -> {
            when(keyStatus) {
                KeyStatus.PRESS -> {
                    postBackground {
                        while (keysStatuses[keyType] != KeyStatus.RELEASE) {
                            if(keysStatuses[KeyType.S] != KeyStatus.RELEASE) continue
                            moveGameObject { oldPosition, delta ->
                                Point(
                                    x = gameObject.position.x,
                                    y = oldPosition.y - delta.toFloat()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
private fun onRender(canvas: Canvas) {
//    while (System.nanoTime() - timeLast < timeFrame);
    timeLast = System.nanoTime()

    canvas.drawRectangle(
        color = Color.GREEN,
        pointTopLeft = gameObject.position,
        size = Size(width = 24, height = 24)
    )

    canvas.drawText(
        fullPathFont = ResourceProvider.getResourceAsFile("font.main.ttf").absolutePath,
        color = Color.GREEN,
        pointTopLeft = Point(300, 300),
        text = "${gameObject.position}",
        fontHeight = 16f
    )
}
