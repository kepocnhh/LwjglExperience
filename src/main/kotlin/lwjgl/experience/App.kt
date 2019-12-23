package lwjgl.experience

import lwjgl.canvas.Canvas
import lwjgl.engine.Engine
import lwjgl.engine.EngineInputCallback
import lwjgl.engine.EngineInputState
import lwjgl.engine.EngineLogic
import lwjgl.engine.EngineProperty
import lwjgl.engine.FunctionKey
import lwjgl.engine.PrintableKey
import lwjgl.entity.*
import lwjgl.game.pingpong.PingpongEngineLogic
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.resource.ResourceProvider
import org.lwjgl.Version

private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

private const val nanoInSecond = 1_000_000_000L
//private const val framesPerSecond = 120
private const val framesPerSecond = 60
//private const val framesPerSecond = 30
private const val gameObjectPxPerSecond = 100
private const val gameObjectAcceleration = gameObjectPxPerSecond.toDouble() / nanoInSecond

private object SimpleEngineLogic: EngineLogic {
    private class GameObject(
        var position: Point
    )
    private val gameObject = GameObject(position = Point(x = 0, y = 0))

    override val framesPerSecondExpected: Int = framesPerSecond
    private lateinit var shouldEngineStopUnit: Unit
    override val shouldEngineStop: Boolean get() = ::shouldEngineStopUnit.isInitialized

    override val engineInputCallback = object: EngineInputCallback {
        override fun onPrintableKey(key: PrintableKey, status: KeyStatus) {
            //todo
        }

        override fun onFunctionKey(key: FunctionKey, status: KeyStatus) {
            when(key) {
                FunctionKey.ESCAPE -> {
                    when(status) {
                        KeyStatus.RELEASE -> {
                            shouldEngineStopUnit = Unit
                        }
                        else -> Unit//ignored
                    }
                }
                else -> Unit//ignored
            }
        }
    }

    override fun onUpdateState(engineInputState: EngineInputState, engineProperty: EngineProperty) {
        val printableKeys = engineInputState.keyboard.printableKeys
//        val sleepTime = 1_000L.toDouble() / 60
//        Thread.sleep((sleepTime).toLong())
        printableKeys.forEach { (key, status) ->
            when(key) {
                PrintableKey.A -> {
                    when(status) {
                        KeyStatus.PRESS, KeyStatus.REPEAT -> {
                            when(printableKeys[PrintableKey.D]) {
                                KeyStatus.PRESS, KeyStatus.REPEAT -> return@forEach
                                else -> Unit//ignored
                            }
                            val timeDifference = engineProperty.timeNow - engineProperty.timeLast
                            val delta = timeDifference * gameObjectAcceleration
                            gameObject.position = Point(
                                x = gameObject.position.x - delta.toFloat(),
                                y = gameObject.position.y
                            )
                        }
                    }
                }
                PrintableKey.D -> {
                    when(status) {
                        KeyStatus.PRESS, KeyStatus.REPEAT -> {
                            when(printableKeys[PrintableKey.A]) {
                                KeyStatus.PRESS, KeyStatus.REPEAT -> return@forEach
                                else -> Unit//ignored
                            }
                            val timeDifference = engineProperty.timeNow - engineProperty.timeLast
                            val delta = timeDifference * gameObjectAcceleration
                            gameObject.position = Point(
                                x = gameObject.position.x + delta.toFloat(),
                                y = gameObject.position.y
                            )
                        }
                    }
                }
                PrintableKey.S -> {
                    when(status) {
                        KeyStatus.PRESS, KeyStatus.REPEAT -> {
                            when(printableKeys[PrintableKey.W]) {
                                KeyStatus.PRESS, KeyStatus.REPEAT -> return@forEach
                                else -> Unit//ignored
                            }
                            val timeDifference = engineProperty.timeNow - engineProperty.timeLast
                            val delta = timeDifference * gameObjectAcceleration
                            gameObject.position = Point(
                                x = gameObject.position.x,
                                y = gameObject.position.y + delta.toFloat()
                            )
                        }
                    }
                }
                PrintableKey.W -> {
                    when(status) {
                        KeyStatus.PRESS, KeyStatus.REPEAT -> {
                            when(printableKeys[PrintableKey.S]) {
                                KeyStatus.PRESS, KeyStatus.REPEAT -> return@forEach
                                else -> Unit//ignored
                            }
                            val timeDifference = engineProperty.timeNow - engineProperty.timeLast
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

    override fun onRender(
        canvas: Canvas,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {

        val fps = nanoInSecond.toDouble() / (engineProperty.timeNow - engineProperty.timeLast)

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
    Engine.run(SimpleEngineLogic)
//    Engine.run(PingpongEngineLogic)
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
