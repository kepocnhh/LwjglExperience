package lwjgl.util.glfw.key

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback

fun glfwKeyCallback(
    onKeyCallback: (Long, Int, Int, Int, Int) -> Unit
) = object: GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        onKeyCallback(window, key, scancode, action, mods)
    }
}


enum class KeyType {
    A,
    D,
    S,
    W,
    ESCAPE,
}

fun Int.toKeyTypeOrNull(): KeyType? {
    return when(this) {
        GLFW.GLFW_KEY_ESCAPE -> KeyType.ESCAPE
        GLFW.GLFW_KEY_A -> KeyType.A
        GLFW.GLFW_KEY_D -> KeyType.D
        GLFW.GLFW_KEY_S -> KeyType.S
        GLFW.GLFW_KEY_W -> KeyType.W
        else -> null
    }
}

enum class KeyStatus {
    RELEASE,
    PRESS,
    REPEAT
}

fun Int.toKeyStatusOrNull(): KeyStatus? {
    return when(this) {
        GLFW.GLFW_RELEASE -> KeyStatus.RELEASE
        GLFW.GLFW_PRESS -> KeyStatus.PRESS
        GLFW.GLFW_REPEAT -> KeyStatus.REPEAT
        else -> null
    }
}