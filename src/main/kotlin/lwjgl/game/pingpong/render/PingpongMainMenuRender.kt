package lwjgl.game.pingpong.render

import lwjgl.canvas.Canvas
import lwjgl.engine.EngineInputState
import lwjgl.engine.EngineProperty
import lwjgl.entity.Point
import lwjgl.game.pingpong.PingpongGameSettings
import lwjgl.game.pingpong.PingpongGameState
import lwjgl.game.pingpong.getAvailableMenuItems
import lwjgl.game.pingpong.drawText

class PingpongMainMenuRender: PingpongRender {
    override fun onRender(
        canvas: Canvas,
        gameState: PingpongGameState,
        gameSettings: PingpongGameSettings,
        engineInputState: EngineInputState,
        engineProperty: EngineProperty
    ) {
        val menuItemHeight = 24
        val availableMenuItems = gameState.getAvailableMenuItems()
        val menuItemTopY = engineProperty.pictureSize.height / 2 - availableMenuItems.size * menuItemHeight / 2
        availableMenuItems.forEachIndexed { index, menuItem ->
            val menuItemY = menuItemTopY + index * menuItemHeight
            if(menuItem == gameState.mainMenu.selectedMenuItem) {
                canvas.drawLine(
                    color = gameSettings.defaultColor,
                    point1 = Point(x = menuItemHeight / 2, y = menuItemY),
                    point2 = Point(x = menuItemHeight / 2, y = menuItemY + menuItemHeight)
                )
            }
            canvas.drawText(gameSettings,
                pointTopLeft = Point(
                    x = menuItemHeight.toFloat(),
                    y = menuItemY + (menuItemHeight - gameSettings.defaultFontHeight)/2
                ),
                text = menuItem.name
            )
        }
    }
}
