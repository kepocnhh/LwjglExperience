package lwjgl.game.pingpong.render

import lwjgl.canvas.Canvas
import lwjgl.engine.EngineInputState
import lwjgl.engine.EngineProperty
import lwjgl.entity.Point
import lwjgl.entity.Size
import lwjgl.entity.square
import lwjgl.game.pingpong.PingpongGameSettings
import lwjgl.game.pingpong.PingpongGameState

class PingpongCompetitionRender: PingpongRender {
    override fun onRender(
        canvas: Canvas,
        gameState: PingpongGameState,
        gameSettings: PingpongGameSettings,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {
        val tableTopLeft: Point
        val tableSize: Size
        if(engineProperty.pictureSize.width > engineProperty.pictureSize.height) {
            val height = engineProperty.pictureSize.height * 0.8
            val width = height / 2 * 3
            tableSize = Size(
                width = width.toInt(),
                height = height.toInt()
            )
            tableTopLeft = Point(
                x = (engineProperty.pictureSize.width - width) / 2,
                y = (engineProperty.pictureSize.height - height) / 2
            )
        } else {
            TODO()
        }

        canvas.drawRectangle(
            gameSettings.defaultColor,
            pointTopLeft = tableTopLeft,
            size = tableSize
        )

        val ballSize = square(20)
        val ballRelativeWidth = tableSize.width - ballSize.width
        val ballRelativeHeight = tableSize.height - ballSize.height
        val ballX = tableTopLeft.x + ballRelativeWidth * gameState.competition.environment.ballCoordinate.xPercent
        val ballY = tableTopLeft.y + ballRelativeHeight * gameState.competition.environment.ballCoordinate.yPercent
        canvas.drawRectangle(
            gameSettings.defaultColor,
            pointTopLeft = Point(
                x = ballX,
                y = ballY
            ),
            size = ballSize
        )

        val playerRacketSize = Size(
            width = 16,
            height = 64
        )
        val playerRacketRelativeHeight = tableSize.height - playerRacketSize.height

        val playerLeftX = tableTopLeft.x - playerRacketSize.width
        val playerLeftY = tableTopLeft.y + playerRacketRelativeHeight * gameState.competition.environment.playerLeftYPercent.value
        canvas.drawRectangle(
            gameSettings.defaultColor,
            pointTopLeft = Point(
                x = playerLeftX,
                y = playerLeftY.toFloat()
            ),
            size = playerRacketSize
        )
        canvas.drawLine(
            gameSettings.defaultColor,
            point1 = Point(
                x = 0.0,
                y = playerLeftY + playerRacketSize.height / 2
            ),
            point2 = Point(
                x = engineProperty.pictureSize.width.toDouble(),
                y = playerLeftY + playerRacketSize.height / 2
            )
        )

        //debug

        canvas.drawLine(
            gameSettings.defaultColor,
            point1 = Point(x = 0, y = engineProperty.pictureSize.height / 2),
            point2 = Point(
                x = engineProperty.pictureSize.width,
                y = engineProperty.pictureSize.height / 2
            )
        )

        canvas.drawLine(
            gameSettings.defaultColor,
            point1 = Point(
                x = engineProperty.pictureSize.width / 2,
                y = 0
            ),
            point2 = Point(
                x = engineProperty.pictureSize.width / 2,
                y = engineProperty.pictureSize.height
            )
        )
    }
}
