package lwjgl.game.pingpong

import lwjgl.entity.Color

class MutablePingpongGameSettings(
    override var fullPathFontMain: String,
    override var defaultColor: Color,
    override var defaultFontHeight: Float
) : PingpongGameSettings
