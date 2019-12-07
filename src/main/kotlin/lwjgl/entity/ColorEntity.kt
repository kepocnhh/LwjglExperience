package lwjgl.entity

const val MAX_VALUE = 1f
const val MIN_VALUE = 0f

data class Color(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float = MAX_VALUE
) {
    companion object {
        val BLACK = Color(MIN_VALUE, MIN_VALUE, MIN_VALUE)
        val RED = Color(MAX_VALUE, MIN_VALUE, MIN_VALUE)
        val GREEN = Color(MIN_VALUE, MAX_VALUE, MIN_VALUE)
        val BLUE = Color(MIN_VALUE, MIN_VALUE, MAX_VALUE)
        val WHITE = Color(MAX_VALUE, MAX_VALUE, MAX_VALUE)
        val TRANSPARENT = Color(MIN_VALUE, MIN_VALUE, MIN_VALUE, MIN_VALUE)
    }

    init {
        assertColorValue(red)
        assertColorValue(green)
        assertColorValue(blue)
        if(alpha < MIN_VALUE || alpha > MAX_VALUE) throw IllegalStateException()
    }
}

private fun assertColorValue(colorValue: Float) {
    if(colorValue < MIN_VALUE || colorValue > MAX_VALUE) throw IllegalStateException()
}