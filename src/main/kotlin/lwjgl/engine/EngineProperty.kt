package lwjgl.engine

import lwjgl.entity.Size

data class EngineProperty(
    val timeLast: Long,
    val timeNow: Long,
    val pictureSize: Size
)
