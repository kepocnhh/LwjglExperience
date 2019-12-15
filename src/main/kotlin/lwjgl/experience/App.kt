package lwjgl.experience

import lwjgl.canvas.Canvas
import lwjgl.entity.*
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.KeyType
import lwjgl.util.glfw.key.toKeyStatusOrNull
import lwjgl.util.glfw.key.toKeyType
import lwjgl.util.resource.ResourceProvider
import lwjgl.window.closeWindow
import lwjgl.window.loopWindow
import org.lwjgl.Version
import java.io.File

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
        fullPathFont = ResourceProvider.getResourceAsFile(File.separator + "font.main.ttf").absolutePath,
        fontHeight = 24f,
        pointTopLeft = Point(50f, 50f),
        color = Color.BLUE,
        text = "poiret 24 50x50 blue"
    )
//    canvas.drawText(
//        fullPathFont = File("resources/font.main.ttf").absolutePath,
//        fontHeight = 28f,
//        pointTopLeft = Point(100f, 100f),
//        color = Color.RED,
//        text = "main 28 100x100 red"
//    )
//    canvas.drawText(
//        fullPathFont = File("resources/font.main.ttf").absolutePath,
//        fontHeight = 20f,
//        pointTopLeft = Point(2, 18),
//        color = Color.GREEN,
//        text = "sfmono 20 2x18 green"
//    )

    canvas.drawText(
        fullPathFont = ResourceProvider.getResourceAsFile(File.separator + "font.consolas.ttf").absolutePath,
        fontHeight = 16f,
        pointTopLeft = Point(0, 0),
        color = Color.BLACK,
        text = "consolas 16 0x0 black"
    )

    canvas.drawRectangle(
        color = Color(1f, 0f, 1f),
        pointTopLeft = Point(50f, 50f),
        size = Size(150, 50)
    )

    val h = 48
    canvas.drawRectangle(
        color = Color(0f, 1f, 1f),
        pointTopLeft = Point(0, 300),
        size = Size(300, h+1)
    )
    canvas.drawRectangle(
        color = Color.RED,
        pointTopLeft = Point(0, 300),
        size = Size(300, h/4)
    )
    canvas.drawText(
        fullPathFont = ResourceProvider.getResourceAsFile(File.separator + "font.main.ttf").absolutePath,
        fontHeight = 24f,
        pointTopLeft = Point(0, 300),
        color = Color.BLACK,
        text = "jqQбВГдДЁуУфФцЦщЩъЪ\nmain 24 0x300 black"
    )
    canvas.drawRectangle(
        color = Color(1f, 0f, 1f),
        pointTopLeft = Point(0, 300),
        size = Size(300, h/2 + 1)
    )

//    canvas.drawText(
//        fullPathFont = File("resources/font.main.ttf").absolutePath,
//        fontHeight = 24f,
//        pointTopLeft = Point(0, 300 + h*2),
//        color = Color.BLACK,
//        text = "!@#$%^&*()"
//    )
    canvas.drawRectangle(
        color = Color.RED,
        pointTopLeft = Point(0, 300 + h*2),
        size = Size(300, h)
    )
}
