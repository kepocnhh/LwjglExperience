package lwjgl.entity

data class Point(
    val x: Float,
    val y: Float
) {
    constructor(x: Int, y: Int): this(x.toFloat(), y.toFloat())
}