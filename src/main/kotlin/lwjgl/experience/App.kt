package lwjgl.experience

import lwjgl.canvas.Canvas
import lwjgl.engine.Engine
import lwjgl.engine.EngineLogic
import lwjgl.engine.EngineRenderProperty
import lwjgl.entity.*
import lwjgl.game.pingpong.PingpongEngineLogic
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

private const val nanoInSecond = 1_000_000_000L
private const val framesPerSecond = 60
//private const val framesPerSecond = 30
private const val gameObjectPxPerSecond = 100
private const val gameObjectAcceleration = gameObjectPxPerSecond.toDouble() / nanoInSecond

private object SimpleEngineLogic: EngineLogic {
    override val framesPerSecondExpected: Int = framesPerSecond

    override fun onKeyCallback(windowId: Long, keyType: KeyType, keyStatus: KeyStatus) {
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

    override fun onRender(
        windowId: Long,
        canvas: Canvas,
        keysStatuses: Map<KeyType, KeyStatus>,
        renderProperty: EngineRenderProperty
    ) {
        keysStatuses.forEach { (keyType, keyStatus) ->
            when(keyType) {
                KeyType.A -> {
                    when(keyStatus) {
                        KeyStatus.PRESS, KeyStatus.REPEAT -> {
                            when(keysStatuses[KeyType.D]) {
                                KeyStatus.PRESS, KeyStatus.REPEAT -> return@forEach
                                else -> Unit//ignored
                            }
                            val timeDifference = System.nanoTime() - renderProperty.timeLast
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
                            when(keysStatuses[KeyType.A]) {
                                KeyStatus.PRESS, KeyStatus.REPEAT -> return@forEach
                                else -> Unit//ignored
                            }
                            val timeDifference = System.nanoTime() - renderProperty.timeLast
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
                            val timeDifference = System.nanoTime() - renderProperty.timeLast
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
                            val timeDifference = System.nanoTime() - renderProperty.timeLast
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

        val fps = nanoInSecond.toDouble() / (renderProperty.timeNow - renderProperty.timeLast)

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
//    Engine.run(SimpleEngineLogic)
    Engine.run(PingpongEngineLogic)
}

private class GameObject(
    var position: Point
)
private val gameObject = GameObject(position = Point(x = 0, y = 0))

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
