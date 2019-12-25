package lwjgl.game.pingpong

import lwjgl.canvas.Canvas
import lwjgl.engine.*
import lwjgl.entity.*
import lwjgl.game.pingpong.render.PingpongCompetitionRender
import lwjgl.game.pingpong.render.PingpongMainMenuRender
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.resource.ResourceProvider
import lwjgl.game.pingpong.render.PingpongDebugRender
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

object PingpongEngineLogic: EngineLogic {
    private val mutableGameState = MutablePingpongGameState()
    private val mutableGameSettings = MutablePingpongGameSettings(
        fullPathFontMain = ResourceProvider.getResourceAsFile("font.main.ttf").absolutePath,
        defaultColor = Color.GREEN,
        defaultFontHeight = 16f
    )

    override val framesPerSecondExpected: Int = 60
    override val shouldEngineStop: Boolean get() {
        return mutableGameState.shouldEngineStop
    }

    override val engineInputCallback = PingpongEngineInputCallback(
        mutableGameState,
        mutableGameSettings
    )

    override fun onPreLoop() {
//        mutableGameSettings.isDebugEnabled = true
        mutableGameSettings.timeStart = System.nanoTime()
    }

    private fun proxyPercent(value: Double): Double {
        return when {
            value < 0.0 -> 0.0
            value > 1.0 -> 1.0
            else -> value
        }
    }
    override fun onUpdateState(
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {
        val gameSate: PingpongGameState = mutableGameState
        when(gameSate.common) {
            PingpongGameState.Common.MAIN_MENU -> {
                //todo
            }
            PingpongGameState.Common.COMPETITION -> {
                val environment = mutableGameState.competition.environment
                checkNotNull(environment)
                val timeDifference = engineProperty.timeNow - engineProperty.timeLast
                when(environment.state) {
                    PingpongGameState.Competition.Environment.State.GAME -> {
                        if(!environment.isPaused) {
                            val ballAcceleration = mutableGameSettings.ballPercentPerSecond.value / Engine.nanoInSecond
                            val ballDirectionValue = environment.ballDirection.value
                            val delta = timeDifference * ballAcceleration
                            val oldY = environment.ballCoordinate.yPercent
                            val newY = oldY + sin(ballDirectionValue) * delta
                            if(newY > 1 || newY < 0) {
                                environment.ballDirection = Degrees(-ballDirectionValue)
                            }
                            val oldX = environment.ballCoordinate.xPercent
                            val newX = oldX + cos(ballDirectionValue) * delta
                            if(newX <= 0.0) {
                                val tableSize = getTableSize(engineProperty.pictureSize)
                                val tableTopLeft = getTableTopLeft(engineProperty.pictureSize, tableSize = tableSize)
                                val playerRacketSize = getPlayerRacketSize()
                                val playerRacketRelativeHeight = tableSize.height - playerRacketSize.height
                                val playerLeftY = tableTopLeft.y + playerRacketRelativeHeight * environment.playerLeftYPercent.value
                                val ballSize = getBallSize()
                                val ballRelativeHeight = tableSize.height - ballSize.height
                                val ballY = tableTopLeft.y + ballRelativeHeight * environment.ballCoordinate.yPercent
                                if(ballY >= playerLeftY && ballY <= playerLeftY + playerRacketSize.height
                                    || ballY + ballSize.height >= playerLeftY && ballY + ballSize.height <= playerLeftY + playerRacketSize.height) {
                                    val additionalDegrees = Random.nextDouble(from = -Degrees.maxDegreesValue/12, until = Degrees.maxDegreesValue/12)
                                    val newValue = Degrees.maxDegreesValue/2-ballDirectionValue + additionalDegrees
                                    when {
                                        newValue < -Degrees.maxDegreesValue/8 -> {
                                            environment.ballDirection = Degrees(
                                                Random.nextDouble(from = -Degrees.maxDegreesValue/8, until = 0.0)
                                            )
                                        }
                                        newValue > Degrees.maxDegreesValue/8 -> {
                                            environment.ballDirection = Degrees(
                                                Random.nextDouble(from = 0.0, until = Degrees.maxDegreesValue/8)
                                            )
                                        }
                                        else -> {
                                            environment.ballDirection = Degrees(newValue)
                                        }
                                    }
                                } else {
                                    environment.playerLeftYPercent = Percent(0.5)
                                    environment.state = PingpongGameState.Competition.Environment.State.PITCH
                                }
                            } else if(newX >= 1.0) {
                                val additionalDegrees = Random.nextDouble(from = -Degrees.maxDegreesValue/12, until = Degrees.maxDegreesValue/12)
                                environment.ballDirection = Degrees(Degrees.maxDegreesValue/2-ballDirectionValue + additionalDegrees)
                            }
                            environment.ballCoordinate = PointPercent(
                                xPercent = proxyPercent(newX),
                                yPercent = proxyPercent(newY)
                            )
                        }
                    }
                }

                when(engineInputState.keyboard.printableKeys[PrintableKey.W]) {
                    KeyStatus.PRESS -> {
                        if(engineInputState.keyboard.printableKeys[PrintableKey.S] == KeyStatus.PRESS) return
                        if(environment.playerLeftYPercent.value == 0.0) return
                        val playerRacketAcceleration = mutableGameSettings.playerRacketPercentPerSecond.value / Engine.nanoInSecond
                        val delta = timeDifference * playerRacketAcceleration
                        val oldValue = environment.playerLeftYPercent.value
                        val newValue = oldValue - delta
                        environment.playerLeftYPercent = Percent(proxyPercent(newValue))
                    }
                }
                when(engineInputState.keyboard.printableKeys[PrintableKey.S]) {
                    KeyStatus.PRESS -> {
                        if(engineInputState.keyboard.printableKeys[PrintableKey.W] == KeyStatus.PRESS) return
                        checkNotNull(environment)
                        if(environment.playerLeftYPercent.value == 1.0) return
                        val playerRacketAcceleration = mutableGameSettings.playerRacketPercentPerSecond.value / Engine.nanoInSecond
                        val delta = timeDifference * playerRacketAcceleration
                        val oldValue = environment.playerLeftYPercent.value
                        val newValue = oldValue + delta
                        environment.playerLeftYPercent = Percent(proxyPercent(newValue))
                    }
                }
            }
        }
    }

    private val mainMenuRender = PingpongMainMenuRender()
    override fun onRender(
        canvas: Canvas,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {
        val gameSate: PingpongGameState = mutableGameState
        val gameSettings: PingpongGameSettings = mutableGameSettings
        val render = when(gameSate.common) {
            PingpongGameState.Common.MAIN_MENU -> mainMenuRender
            PingpongGameState.Common.COMPETITION -> PingpongCompetitionRender
        }
        render.onRender(
            canvas, gameSate, gameSettings, engineInputState, engineProperty
        )
        if(gameSettings.isDebugEnabled) {
            PingpongDebugRender.onRender(
                canvas, gameSate, gameSettings, engineInputState, engineProperty
            )
        }
    }
}

fun Canvas.drawText(
    gameSettings: PingpongGameSettings,
    pointTopLeft: Point,
    text: CharSequence
) {
    drawText(
        fullPathFont = gameSettings.fullPathFontMain,
        color = gameSettings.defaultColor,
        pointTopLeft = pointTopLeft,
        text = text,
        fontHeight = gameSettings.defaultFontHeight
    )
}
