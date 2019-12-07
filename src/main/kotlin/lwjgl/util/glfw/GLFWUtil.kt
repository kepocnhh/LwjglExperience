package lwjgl.util.glfw

import lwjgl.entity.Size
import lwjgl.util.io.use
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil

fun glfwGetWindowSize(windowId: Long) = stackPush().use {
    glfwGetWindowSize(windowId, stack = it)
}
fun glfwGetWindowSize(windowId: Long, stack: MemoryStack): Size {
    val widthBuffer = stack.mallocInt(1)
    val heightBuffer = stack.mallocInt(1)
    glfwGetWindowSize(windowId, widthBuffer, heightBuffer)
    return Size(widthBuffer[0], heightBuffer[0])
}

fun glfwGetMonitorContentScale(monitorId: Long) = stackPush().use {
    glfwGetMonitorContentScale(monitorId, stack = it)
}
fun glfwGetMonitorContentScale(monitorId: Long, stack: MemoryStack): Pair<Float, Float> {
    val px = stack.mallocFloat(1)
    val py = stack.mallocFloat(1)
    glfwGetMonitorContentScale(monitorId, px, py)
    return px[0] to py[0]
}