package lwjgl.game.pingpong

import lwjgl.entity.Color
import lwjgl.entity.Percent

class MutablePingpongGameSettings(
    override var fullPathFontMain: String,
    override var defaultColor: Color,
    override var defaultFontHeight: Float
) : PingpongGameSettings {
    override val playerRacketPercentPerSecond = Percent(0.2)
}
