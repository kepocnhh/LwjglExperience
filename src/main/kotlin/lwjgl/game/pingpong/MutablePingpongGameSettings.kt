package lwjgl.game.pingpong

import lwjgl.entity.Color
import lwjgl.entity.Percent

class MutablePingpongGameSettings(
    override var fullPathFontMain: String,
    override var defaultColor: Color,
    override var defaultFontHeight: Float
) : PingpongGameSettings {
    override val playerRacketPercentPerSecond = Percent(0.4)
    override val ballPercentPerSecond = Percent(0.5)
    override var isDebugEnabled: Boolean = false
    override var timeStart: Long = -1
}
