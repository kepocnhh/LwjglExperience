package lwjgl.engine

import lwjgl.util.glfw.key.KeyStatus
import org.lwjgl.glfw.GLFW

interface EngineInputState {
    interface Keyboard {
        val printableKeys: Map<PrintableKey, KeyStatus>
        val functionKeys: Map<FunctionKey, KeyStatus>
    }

    val keyboard: Keyboard
}

enum class PrintableKey {
    A,
    D,
    S,
    W,
    U,
}

fun Int.toPrintableKeyOrNull(): PrintableKey? {
    return when(this) {
        GLFW.GLFW_KEY_A -> PrintableKey.A
        GLFW.GLFW_KEY_D -> PrintableKey.D
        GLFW.GLFW_KEY_S -> PrintableKey.S
        GLFW.GLFW_KEY_W -> PrintableKey.W
        GLFW.GLFW_KEY_U -> PrintableKey.U
        else -> null
    }
}

enum class FunctionKey {
    ESCAPE,
    ENTER,
    SPACE,
}

fun Int.toFunctionKeyOrNull(): FunctionKey? {
    return when(this) {
        GLFW.GLFW_KEY_ESCAPE -> FunctionKey.ESCAPE
        GLFW.GLFW_KEY_ENTER -> FunctionKey.ENTER
        GLFW.GLFW_KEY_SPACE -> FunctionKey.SPACE
        else -> null
    }
}

interface EngineInputCallback {
    fun onPrintableKey(key: PrintableKey, status: KeyStatus)
    fun onFunctionKey(key: FunctionKey, status: KeyStatus)
}