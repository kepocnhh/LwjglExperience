package lwjgl.util.glfw.primitive

import org.lwjgl.glfw.GLFW.*

fun Boolean.toGLFWInt(): Int {
    return if(this) GLFW_TRUE else GLFW_FALSE
}