package lwjgl.game.pingpong.render

import lwjgl.canvas.Canvas
import lwjgl.engine.Engine
import lwjgl.engine.EngineInputState
import lwjgl.engine.EngineProperty
import lwjgl.entity.Point
import lwjgl.entity.square
import lwjgl.game.pingpong.*

object PingpongDebugRender: PingpongRender {
    private val fpsValueQueue = mutableMapOf<Long, Double>()

    override fun onRender(
        canvas: Canvas,
        gameState: PingpongGameState,
        gameSettings: PingpongGameSettings,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {
        val fpsRecordTime = Engine.nanoInSecond * (engineProperty.pictureSize.width.toDouble() / 200)
        val fps = Engine.nanoInSecond.toDouble() / (engineProperty.timeNow - engineProperty.timeLast)
        val max = fpsValueQueue.keys.max()
        if(max == null || max < engineProperty.timeNow - Engine.nanoInSecond * 0.5) {
            fpsValueQueue.keys.toList().forEach {
                if(it < engineProperty.timeNow - fpsRecordTime)
                    fpsValueQueue.remove(it)
            }
            fpsValueQueue[engineProperty.timeNow] = fps
        }
        fpsValueQueue.keys.sorted().forEach { time ->
            val value = fpsValueQueue[time]
            val dif = (engineProperty.timeNow - time).toDouble()
            val x = engineProperty.pictureSize.width.toDouble() - (100 * dif / Engine.nanoInSecond)
            if(value != null && x > engineProperty.pictureSize.width/2) {
                canvas.drawRectangle(
                    color = gameSettings.defaultColor,
                    pointTopLeft = Point(
                        x = x,
                        y = engineProperty.pictureSize.height - ((engineProperty.pictureSize.height.toDouble()/2) * (value / PingpongEngineLogic.framesPerSecondExpected))
                    ),
                    size = square(5)
                )
                val timeDiffInSecond = (time - gameSettings.timeStart).toDouble()/ Engine.nanoInSecond
                canvas.drawText(gameSettings,
                    pointTopLeft = Point(
                        x = x.toFloat(),
                        y = engineProperty.pictureSize.height - gameSettings.defaultFontHeight
                    ),
                    text = "${(timeDiffInSecond*10).toInt().toDouble()/10}"
                )
            }
        }

        canvas.drawText(gameSettings,
            pointTopLeft = Point(0f, gameSettings.defaultFontHeight * 0),
            text = "${(fps*100).toInt().toDouble()/100}"
        )

        canvas.drawText(gameSettings,
            pointTopLeft = Point(0f, gameSettings.defaultFontHeight * 1),
            text = "${engineProperty.pictureSize.width}x${engineProperty.pictureSize.height}"
        )

        val timeStartDiffInSecond = (engineProperty.timeNow - gameSettings.timeStart).toDouble()/ Engine.nanoInSecond
        canvas.drawText(gameSettings,
            pointTopLeft = Point(0f, gameSettings.defaultFontHeight * 2),
            text = "${(timeStartDiffInSecond*10).toInt().toDouble()/10}s"
        )

        canvas.drawLine(
            gameSettings.defaultColor,
            point1 = Point(
                x = 0,
                y = engineProperty.pictureSize.height / 2
            ),
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
