package lwjgl.entity

data class Size(
    val width: Int,
    val height: Int
)

fun square(size: Int) = Size(size, size)
