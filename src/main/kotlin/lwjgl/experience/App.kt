package lwjgl.experience

import lwjgl.canvas.Canvas
import lwjgl.entity.*
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.KeyType
import lwjgl.util.glfw.key.toKeyStatusOrNull
import lwjgl.util.glfw.key.toKeyTypeOrNull
import lwjgl.util.glfw.opengl.glClearColor
import lwjgl.util.resource.ResourceProvider
import lwjgl.window.WindowSize
import lwjgl.window.closeWindow
import lwjgl.window.loopWindow
import org.lwjgl.Version
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.math.roundToInt

private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
private val keysStatuses = mutableMapOf<KeyType, KeyStatus>().also { statuses ->
    KeyType.values().forEach { keyType ->
        statuses[keyType] = KeyStatus.RELEASE
    }
}
private var timeLast = 0L
private const val nanoInSecond = 1_000_000_000L
private const val framesPerSecond = 60L
//private const val framesPerSecond = 30L
private const val gameObjectPxPerSecond = 100
private const val gameObjectAcceleration = gameObjectPxPerSecond.toDouble() / nanoInSecond
private const val timeFrame = nanoInSecond.toDouble() / framesPerSecond
private fun onKeyCallback() {
    keysStatuses.forEach { (keyType, keyStatus) ->
        when(keyType) {
            KeyType.A -> {
                when(keyStatus) {
                    KeyStatus.PRESS, KeyStatus.REPEAT -> {
                        val timeDifference = System.nanoTime() - timeLast
                        val delta = timeDifference * gameObjectAcceleration
                        gameObject.position = Point(
                            x = gameObject.position.x - delta.toFloat(),
                            y = gameObject.position.y
                        )
                    }
                }
            }
            KeyType.D -> {
                when(keyStatus) {
                    KeyStatus.PRESS, KeyStatus.REPEAT -> {
                        val timeDifference = System.nanoTime() - timeLast
                        val delta = timeDifference * gameObjectAcceleration
                        gameObject.position = Point(
                            x = gameObject.position.x + delta.toFloat(),
                            y = gameObject.position.y
                        )
                    }
                }
            }
            KeyType.S -> {
                when(keyStatus) {
                    KeyStatus.PRESS, KeyStatus.REPEAT -> {
                        val timeDifference = System.nanoTime() - timeLast
                        val delta = timeDifference * gameObjectAcceleration
                        gameObject.position = Point(
                            x = gameObject.position.x,
                            y = gameObject.position.y + delta.toFloat()
                        )
                    }
                }
            }
            KeyType.W -> {
                when(keyStatus) {
                    KeyStatus.PRESS, KeyStatus.REPEAT -> {
                        val timeDifference = System.nanoTime() - timeLast
                        val delta = timeDifference * gameObjectAcceleration
                        gameObject.position = Point(
                            x = gameObject.position.x,
                            y = gameObject.position.y - delta.toFloat()
                        )
                    }
                }
            }
        }
    }
}
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
    loopWindow(
        windowSize = WindowSize.Exact(size = Size(width = 640, height = 480)),
//        windowSize = WindowSize.FullScreen,// todo windows cmd issue + fullscreen toggle issue
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
            //todo
        },
        onRender = ::onRender
    )
}

private class GameObject(
    var position: Point
)
private val gameObject = GameObject(position = Point(x = 0, y = 0))

private fun onKeyCallback(windowId: Long, keyType: KeyType, keyStatus: KeyStatus) {
    println("key = $keyType, action = $keyStatus")
    when(keyType) {
        KeyType.ESCAPE -> {
            when(keyStatus) {
                KeyStatus.RELEASE -> {
                    closeWindow(windowId)
                }
                else -> Unit//ignored
            }
        }
    }
}

private val fullPathFontMain = ResourceProvider.getResourceAsFile("font.main.ttf").absolutePath
private fun Canvas.drawText(
    fontHeight: Float,
    pointTopLeft: Point,
    color: Color,
    text: CharSequence
) {
    drawText(
        fullPathFont = fullPathFontMain,
        color = color,
        pointTopLeft = pointTopLeft,
        text = text,
        fontHeight = fontHeight
    )
}
private fun onRender(canvas: Canvas) {
    while (System.nanoTime() - timeLast < timeFrame);

    onKeyCallback()
    val timeNow = System.nanoTime()
    val fps = nanoInSecond.toDouble() / (timeNow - timeLast)
    timeLast = timeNow

    canvas.drawRectangle(
        color = Color.GREEN,
        pointTopLeft = gameObject.position,
        size = Size(width = 24, height = 24)
    )

    canvas.drawText(
        color = Color.GREEN,
        pointTopLeft = Point(300, 300),
        text = "${gameObject.position}",
        fontHeight = 16f
    )

    canvas.drawText(
        color = Color.GREEN,
        pointTopLeft = Point(0, 0),
        text = "${(fps*100).toInt().toDouble()/100}",
        fontHeight = 16f
    )
}
