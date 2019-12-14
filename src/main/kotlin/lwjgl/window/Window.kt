package lwjgl.window

import lwjgl.canvas.Canvas
import lwjgl.entity.*
import lwjgl.util.glfw.glfwGetWindowSize
import lwjgl.util.glfw.key.glfwKeyCallback
import lwjgl.util.glfw.opengl.glColorOf
import lwjgl.util.glfw.opengl.glOrtho
import lwjgl.util.glfw.opengl.glTransaction
import lwjgl.util.glfw.opengl.glVertexOf
import lwjgl.util.glfw.primitive.toGLFWInt
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil
import java.io.PrintStream

private val windows = mutableMapOf<Long, WindowStatus>()

private enum class WindowStatus {
    CREATED,
    LOOPED,
    CLOSED,
    DESTROYED
}

fun createWindow(
    errorPrintStream: PrintStream,
    isVisible: Boolean,
    isResizable: Boolean,
    onKeyCallback: (Long, Int, Int, Int, Int) -> Unit,
    width: Int,
    height: Int,
    title: String,
    monitorIdSupplier: () -> Long
): Pair<Long, Long> {
    println("create window | start")
    GLFWErrorCallback.createPrint(errorPrintStream).set()
    if(!GLFW.glfwInit()) throw IllegalStateException("Unable to initialize GLFW")
    println("create window | init")
    GLFW.glfwDefaultWindowHints()
    GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, isVisible.toGLFWInt())
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, isResizable.toGLFWInt())
    val windowId = GLFW.glfwCreateWindow(
        width,
        height,
        title,
        MemoryUtil.NULL,
        MemoryUtil.NULL
    )
    if(windowId == MemoryUtil.NULL) throw IllegalStateException("Failed to create the GLFW window")
    println("create window | start: $windowId")
    val monitorId = monitorIdSupplier()
    if(monitorId == MemoryUtil.NULL) throw IllegalStateException("Failed to create the GLFW window")
    GLFW.glfwGetVideoMode(monitorId)?.apply {
        GLFW.glfwSetWindowPos(
            windowId,
            (width() - width) / 2,
            (height()- height)/ 2
        )
    }
    GLFW.glfwMakeContextCurrent(windowId)
    GLFW.glfwSwapInterval(1)
    GLFW.glfwShowWindow(windowId)
    println("create window | show: $windowId")
    GLFW.glfwSetKeyCallback(windowId, glfwKeyCallback(onKeyCallback))
    windows[windowId] = WindowStatus.CREATED
    println("create window | finish: $windowId")
    return windowId to monitorId
}

fun closeWindow(windowId: Long) {
    when(windows[windowId]) {
        WindowStatus.CLOSED -> return
        WindowStatus.CREATED, WindowStatus.LOOPED -> Unit
        else -> throw IllegalStateException("Window ($windowId) must be created or looped")
    }
    GLFW.glfwSetWindowShouldClose(windowId, true)
    windows[windowId] = WindowStatus.CLOSED
}

fun destroyWindow(windowId: Long) {
    println("destroy window: $windowId")
    when(windows[windowId]) {
        WindowStatus.DESTROYED -> return
        null -> throw IllegalStateException(
            "Window ($windowId) must be created or looped or closed"
        )
        WindowStatus.CREATED, WindowStatus.LOOPED, WindowStatus.CLOSED -> Unit
    }
    println("destroy window: $windowId | start destroy")
    glfwFreeCallbacks(windowId)
    GLFW.glfwDestroyWindow(windowId)
    GLFW.glfwTerminate()
    GLFW.glfwSetErrorCallback(null)?.free()
    windows[windowId] = WindowStatus.DESTROYED
    println("destroy window: $windowId | finish destroy")
}

private fun onPreRender(windowId: Long) {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    GLFW.glfwPollEvents()
//    GL11.glDisable(GL11.GL_CULL_FACE)
//    GL11.glDisable(GL11.GL_TEXTURE_2D)
//    GL11.glDisable(GL11.GL_LIGHTING)
//    GL11.glDisable(GL11.GL_DEPTH_TEST)

//    GL11.glEnable(GL11.GL_BLEND)
//    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

    val windowSize = glfwGetWindowSize(windowId)
//    GL11.glViewport(0, 0, windowSize.width, windowSize.height)

    GL11.glMatrixMode(GL11.GL_PROJECTION)
    GL11.glLoadIdentity()
    glOrtho(
        rightFrustumPlane = windowSize.width.toDouble(),
        bottomFrustumPlane = windowSize.height.toDouble()
    )
    GL11.glMatrixMode(GL11.GL_MODELVIEW)
    GL11.glLoadIdentity()
}

private fun onPostRender(windowId: Long) {
//    GL11.glDisable(GL11.GL_BLEND)
    GLFW.glfwSwapBuffers(windowId)
}

fun loopWindow(windowId: Long, monitorId: Long, onRender: (Canvas) -> Unit) {
    println("loop window: $windowId")
    when(windows[windowId]) {
        WindowStatus.LOOPED -> throw IllegalStateException("Window ($windowId) already looped")
        WindowStatus.CREATED -> Unit
        else -> throw IllegalStateException("Window ($windowId) must be created")
    }
    GLFW.glfwMakeContextCurrent(windowId)
    GL.createCapabilities()
    GLFW.glfwSwapInterval(1)
    GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
    windows[windowId] = WindowStatus.LOOPED
//    GL11.glEnable(GL11.GL_BLEND)
//    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
//    GL11.glEnable(GL11.GL_TEXTURE_2D)
    val fullPathFont = "/font.consolas.ttf"
    val canvas = WindowCanvas(
        windowId,
//        fontRender = STBFontRender(
        fontRender = STBAdvancedFontRender(
            fullPathFont = fullPathFont,
            fontHeight = 24f
        )
    )
    println("loop window: $windowId | start loop")
    while(!GLFW.glfwWindowShouldClose(windowId)) {
        onPreRender(windowId)
//        GL11.glEnable(GL11.GL_BLEND)
//        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
//        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        onRender(canvas)
        onPostRender(windowId)
    }
    println("loop window: $windowId | finish loop")
}

fun loopWindow(
    width: Int,
    height: Int,
    title: String,
    onKeyCallback: (Long, Int, Int, Int, Int) -> Unit,
    errorPrintStream: PrintStream = System.err,
    isVisible: Boolean = true,
    isResizable: Boolean = false,
    monitorIdSupplier: () -> Long = GLFW::glfwGetPrimaryMonitor,
    onRender: (Canvas) -> Unit
) {
    val (windowId, monitorId) = createWindow(
        errorPrintStream,
        isVisible,
        isResizable,
        onKeyCallback,
        width,
        height,
        title,
        monitorIdSupplier
    )
    loopWindow(windowId, monitorId, onRender)
    destroyWindow(windowId)
}

private class WindowCanvas(
    private val windowId: Long,
    private val fontRender: FontRender
): Canvas {
    override fun drawLine(
        color: Color,
        point1: Point,
        point2: Point
    ) {
        val windowSize = getWindowSize()
        GL11.glLineWidth(1.0f)
        glColorOf(color)
        glTransaction(GL11.GL_LINE_STRIP) {
            glVertexOf(point1)
            glVertexOf(point2)
//            glVertexOf(point1.toWindowPoint(windowSize))
//            glVertexOf(point2.toWindowPoint(windowSize))
        }
    }

    override fun drawRectangle(
        color: Color,
        pointTopLeft: Point,
        size: Size
    ) {
        val windowSize = getWindowSize()
//        val (x1, y1) = pointTopLeft.toWindowPoint(windowSize)
        val (x1, y1) = pointTopLeft
        val (x2, y2) = Point(
            x = pointTopLeft.x + size.width,
            y = pointTopLeft.y + size.height
        )//.toWindowPoint(windowSize)
        glColorOf(color)
        glTransaction(GL11.GL_LINE_LOOP) {
            glVertexOf(x1, y1)
            glVertexOf(x2, y1)
            glVertexOf(x2, y2)
            glVertexOf(x1, y2)
        }
    }

    override fun drawText(
        color: Color, pointTopLeft: Point, text: CharSequence
    ) {
//        val windowSize = getWindowSize()
//        val (x, y) = pointTopLeft.toWindowPoint(windowSize)
//        fontRender.drawText(color, Point(x, y), text)
//        fontRender.drawText(color, pointTopLeft.toWindowPoint(windowSize), text)
        fontRender.drawText(color, pointTopLeft, text)
    }

//    private fun Point.toWindowPoint() = toWindowPoint(windowId)
    private fun getWindowSize() = glfwGetWindowSize(windowId)
}

//private fun Point.toWindowPoint(windowId: Long) = toWindowPoint(glfwGetWindowSize(windowId))
//private fun Point.toWindowPoint(size: Size) = toWindowPoint(size.width, size.height)
//private fun Point.toWindowPoint(width: Int, height: Int) = Point(
//    x = (x/width -0.5f)* 2,
//    y = (y/height-0.5f)*-2
//)