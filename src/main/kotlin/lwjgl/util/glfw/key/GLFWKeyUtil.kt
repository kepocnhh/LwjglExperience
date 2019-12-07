package lwjgl.util.glfw.key

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback

fun glfwKeyCallback(
    onKeyCallback: (Long, Int, Int, Int, Int) -> Unit
) = object: GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        onKeyCallback(window, key, scancode, action, mods)
    }
}


enum class KeyType {
    ESCAPE,
    UNKNOWN
}

fun Int.toKeyType(): KeyType {
    return when(this) {
        GLFW_KEY_ESCAPE -> KeyType.ESCAPE
        else -> KeyType.UNKNOWN
    }
}

enum class KeyStatus {
    RELEASE,
    PRESS,
    REPEAT
}

fun Int.toKeyStatusOrNull(): KeyStatus? {
    return when(this) {
        GLFW_RELEASE -> KeyStatus.RELEASE
        GLFW_PRESS -> KeyStatus.PRESS
        GLFW_REPEAT -> KeyStatus.REPEAT
        else -> null
    }
}