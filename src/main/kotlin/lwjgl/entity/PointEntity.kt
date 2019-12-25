package lwjgl.entity

data class Point(
    val x: Float,
    val y: Float
) {
    constructor(x: Double, y: Double): this(x.toFloat(), y.toFloat())
    constructor(x: Int, y: Int): this(x.toFloat(), y.toFloat())
}

data class PointPercent(
    val xPercent: Double,
    val yPercent: Double
) {
    init {
        check(xPercent in 0.0..1.0)
        check(yPercent in 0.0..1.0)
    }
}

data class Percent(
    val value: Double
) {
    init {
        check(value in 0.0..1.0)
    }
}
