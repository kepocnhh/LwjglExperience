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
}

fun Int.toPrintableKeyOrNull(): PrintableKey? {
    return when(this) {
        GLFW.GLFW_KEY_A -> PrintableKey.A
        GLFW.GLFW_KEY_D -> PrintableKey.D
        GLFW.GLFW_KEY_S -> PrintableKey.S
        GLFW.GLFW_KEY_W -> PrintableKey.W
        else -> null
    }
}

enum class FunctionKey {
    ESCAPE,
    ENTER,
}

fun Int.toFunctionKeyOrNull(): FunctionKey? {
    return when(this) {
        GLFW.GLFW_KEY_ESCAPE -> FunctionKey.ESCAPE
        GLFW.GLFW_KEY_ENTER -> FunctionKey.ENTER
        else -> null
    }
}

interface EngineInputCallback {
    fun onPrintableKey(key: PrintableKey, status: KeyStatus)
    fun onFunctionKey(key: FunctionKey, status: KeyStatus)
}