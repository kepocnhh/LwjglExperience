package lwjgl.game.pingpong

import lwjgl.entity.Color
import lwjgl.entity.Percent

interface PingpongGameSettings {
    val fullPathFontMain: String
    val defaultColor: Color
    val defaultFontHeight: Float
    val playerRacketPercentPerSecond: Percent
}
