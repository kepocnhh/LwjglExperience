package lwjgl.game.pingpong.render

import lwjgl.canvas.Canvas
import lwjgl.engine.EngineInputState
import lwjgl.engine.EngineProperty
import lwjgl.entity.Point
import lwjgl.entity.Size
import lwjgl.entity.square
import lwjgl.game.pingpong.*

object PingpongCompetitionRender: PingpongRender {
    override fun onRender(
        canvas: Canvas,
        gameState: PingpongGameState,
        gameSettings: PingpongGameSettings,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {
        val tableSize = getTableSize(engineProperty.pictureSize)
        val tableTopLeft = getTableTopLeft(engineProperty.pictureSize, tableSize = tableSize)

//        canvas.drawRectangle(
//            gameSettings.defaultColor,
//            pointTopLeft = tableTopLeft,
//            size = tableSize
//        )
        canvas.drawLine(
            gameSettings.defaultColor,
            point1 = tableTopLeft,
            point2 = Point(
                x = tableTopLeft.x + tableSize.width,
                y = tableTopLeft.y
            )
        )
        canvas.drawLine(
            gameSettings.defaultColor,
            point1 = Point(
                x = tableTopLeft.x,
                y = tableTopLeft.y + tableSize.height
            ),
            point2 = Point(
                x = tableTopLeft.x + tableSize.width,
                y = tableTopLeft.y + tableSize.height
            )
        )
        canvas.drawLine(
            gameSettings.defaultColor,
            point1 = Point(
                x = tableTopLeft.x + tableSize.width,
                y = tableTopLeft.y
            ),
            point2 = Point(
                x = tableTopLeft.x + tableSize.width,
                y = tableTopLeft.y + tableSize.height
            )
        )

        val environment = gameState.competition.environment
        checkNotNull(environment)

        val playerRacketSize = getPlayerRacketSize()
        val playerRacketRelativeHeight = tableSize.height - playerRacketSize.height

        val playerLeftX = tableTopLeft.x - playerRacketSize.width
        val playerLeftY = tableTopLeft.y + playerRacketRelativeHeight * environment.playerLeftYPercent.value
        canvas.drawRectangle(
            gameSettings.defaultColor,
            pointTopLeft = Point(
                x = playerLeftX,
                y = playerLeftY.toFloat()
            ),
            size = playerRacketSize
        )
//        canvas.drawLine(
//            gameSettings.defaultColor,
//            point1 = Point(
//                x = 0.0,
//                y = playerLeftY + playerRacketSize.height / 2
//            ),
//            point2 = Point(
//                x = engineProperty.pictureSize.width.toDouble(),
//                y = playerLeftY + playerRacketSize.height / 2
//            )
//        )

        val ballSize = getBallSize()

        when(environment.state) {
            PingpongGameState.Competition.Environment.State.PITCH -> {
                canvas.drawRectangle(
                    gameSettings.defaultColor,
                    pointTopLeft = Point(
                        x = playerLeftX + playerRacketSize.width,
                        y = playerLeftY.toFloat() + playerRacketSize.height/2 - ballSize.height/2
                    ),
                    size = ballSize
                )
            }
            PingpongGameState.Competition.Environment.State.GAME -> {
                val ballRelativeWidth = tableSize.width - ballSize.width
                val ballRelativeHeight = tableSize.height - ballSize.height
                val ballX = tableTopLeft.x + ballRelativeWidth * environment.ballCoordinate.xPercent
                val ballY = tableTopLeft.y + ballRelativeHeight * environment.ballCoordinate.yPercent
                canvas.drawRectangle(
                    gameSettings.defaultColor,
                    pointTopLeft = Point(
                        x = ballX,
                        y = ballY
                    ),
                    size = ballSize
                )

                canvas.drawText(gameSettings,
                    pointTopLeft = Point(
                        x = 0.toFloat(),
                        y = engineProperty.pictureSize.height - gameSettings.defaultFontHeight
                    ),
                    text = "ball(${ballX.toInt()}:${ballY.toInt()}, ${environment.ballDirection.value})"
                )
            }
        }
    }
}
