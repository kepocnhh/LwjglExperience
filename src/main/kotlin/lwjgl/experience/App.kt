package lwjgl.experience

import lwjgl.canvas.Canvas
import lwjgl.entity.*
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.KeyType
import lwjgl.util.glfw.key.toKeyStatusOrNull
import lwjgl.util.glfw.key.toKeyType
import lwjgl.window.closeWindow
import lwjgl.window.loopWindow
import org.lwjgl.Version

private val defaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

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
        width = 640,
        height = 480,
        title = "lwjgl.ui",
        onKeyCallback = { windowId, key, _, action, _ ->
            onKeyCallback(
                windowId,
                key.toKeyType(),
                action.toKeyStatusOrNull() ?: throw IllegalStateException(
                    "Action ($action) not supported"
                )
            )
        },
        onRender = ::onRender
    )
}

private fun onKeyCallback(windowId: Long, keyType: KeyType, keyStatus: KeyStatus) {
    println("key = $keyType, action = $keyStatus")
    if(keyType == KeyType.ESCAPE && keyStatus == KeyStatus.RELEASE) {
        closeWindow(windowId)
    }
}
private fun onRender(canvas: Canvas) {
    canvas.drawLine(
        color = Color.BLACK,
        point1 = Point(25f, 25f),
        point2 = Point(125f, 125f)
    )
    canvas.drawRectangle(
        color = Color.RED,
        pointTopLeft = Point(50f, 50f),
        size = Size(50, 50)
    )

    canvas.drawText(
        color = Color.BLUE,
        pointTopLeft = Point(50f, 50f),
        text = "50x50 blue"
    )
    canvas.drawText(
        color = Color.RED,
        pointTopLeft = Point(100f, 100f),
        text = "100x100 red"
    )
    canvas.drawText(
        color = Color.GREEN,
        pointTopLeft = Point(2, 18),
        text = "2x18 green"
    )

    canvas.drawText(
        color = Color.BLACK,
        pointTopLeft = Point(0, 0),
        text = "0x0 black"
    )

    canvas.drawRectangle(
        color = Color(1f, 0f, 1f),
        pointTopLeft = Point(50f, 50f),
        size = Size(150, 50)
    )


    canvas.drawText(
        color = Color.BLACK,
        pointTopLeft = Point(0, 300),
        text = "abcdefghijklmnopqrstuvwxyz\ntest"
    )
    canvas.drawRectangle(
        color = Color.RED,
        pointTopLeft = Point(0, 300),
        size = Size(150, 24)
    )
    canvas.drawText(
        color = Color.BLACK,
        pointTopLeft = Point(0, 350),
        text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    )
    canvas.drawRectangle(
        color = Color.RED,
        pointTopLeft = Point(0, 350),
        size = Size(150, 24)
    )
}
