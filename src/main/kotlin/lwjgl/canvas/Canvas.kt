package lwjgl.canvas

import lwjgl.entity.Color
import lwjgl.entity.Point
import lwjgl.entity.Size

interface Canvas {
    fun drawLine(color: Color, point1: Point, point2: Point)
    fun drawRectangle(color: Color, pointTopLeft: Point, size: Size)
    fun drawText(color: Color, pointTopLeft: Point, text: CharSequence)
}