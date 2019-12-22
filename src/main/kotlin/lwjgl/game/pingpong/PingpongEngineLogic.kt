package lwjgl.game.pingpong

import lwjgl.canvas.Canvas
import lwjgl.engine.EngineLogic
import lwjgl.engine.EngineRenderProperty
import lwjgl.entity.Color
import lwjgl.entity.Point
import lwjgl.util.glfw.glfwGetWindowSize
import lwjgl.util.glfw.key.KeyStatus
import lwjgl.util.glfw.key.KeyType
import lwjgl.util.resource.ResourceProvider
import lwjgl.window.closeWindow

private val fullPathFontMain = ResourceProvider.getResourceAsFile("font.main.ttf").absolutePath
private val defaultColor = Color.GREEN
private const val defaultFontHeight = 16f
private fun Canvas.drawText(
    pointTopLeft: Point,
    text: CharSequence
) {
    drawText(
        fullPathFont = fullPathFontMain,
        color = defaultColor,
        pointTopLeft = pointTopLeft,
        text = text,
        fontHeight = defaultFontHeight
    )
}

object PingpongEngineLogic: EngineLogic {
    private var gameSate: PingpongGameState = PingpongGameState.MainMenu(
        availableMenuItems = setOf(
            PingpongGameState.MainMenu.MenuItem.START_NEW_GAME,
            PingpongGameState.MainMenu.MenuItem.CONTINUE_GAME,
            PingpongGameState.MainMenu.MenuItem.EXIT
        ),
        selectedMenuItem = PingpongGameState.MainMenu.MenuItem.START_NEW_GAME
    )

    override val framesPerSecondExpected: Int = 60

    override fun onKeyCallback(windowId: Long, keyType: KeyType, keyStatus: KeyStatus) {
        when(val gameSate = gameSate) {
            is PingpongGameState.MainMenu -> {
                when(gameSate.selectedMenuItem) {
                    PingpongGameState.MainMenu.MenuItem.START_NEW_GAME -> {
                        // todo
                    }
                    PingpongGameState.MainMenu.MenuItem.CONTINUE_GAME -> {
                        // todo
                    }
                    PingpongGameState.MainMenu.MenuItem.EXIT -> {
                        when(keyType) {
                            KeyType.ENTER -> {
                                when(keyStatus) {
                                    KeyStatus.PRESS -> {
                                        closeWindow(windowId)
                                    }
                                    else -> Unit//ignored
                                }
                            }
                            else -> Unit//ignored
                        }
                    }
                }
                when(keyType) {
                    KeyType.S -> {
                        when(keyStatus) {
                            KeyStatus.PRESS -> {
                                PingpongEngineLogic.gameSate = PingpongGameState.MainMenu(
                                    availableMenuItems = gameSate.availableMenuItems,
                                    selectedMenuItem = gameSate.availableMenuItems.sortedBy {
                                        it.ordinal
                                    }.firstOrNull {
                                        it.ordinal > gameSate.selectedMenuItem.ordinal
                                    } ?: PingpongGameState.MainMenu.MenuItem.values().first()
                                )
                            }
                            else -> Unit//ignored
                        }
                    }
                    KeyType.W -> {
                        when(keyStatus) {
                            KeyStatus.PRESS -> {
                                PingpongEngineLogic.gameSate = PingpongGameState.MainMenu(
                                    availableMenuItems = gameSate.availableMenuItems,
                                    selectedMenuItem = gameSate.availableMenuItems.sortedBy {
                                        it.ordinal
                                    }.reversed().firstOrNull {
                                        it.ordinal < gameSate.selectedMenuItem.ordinal
                                    } ?: PingpongGameState.MainMenu.MenuItem.values().last()
                                )
                            }
                            else -> Unit//ignored
                        }
                    }
                }
            }
            is PingpongGameState.Game -> TODO()
        }
    }

    override fun onRender(
        windowId: Long,
        canvas: Canvas,
        keysStatuses: Map<KeyType, KeyStatus>,
        renderProperty: EngineRenderProperty
    ) {
        when(val gameSate = gameSate) {
            is PingpongGameState.MainMenu -> {
                val windowSize = glfwGetWindowSize(windowId)
                val availableMenuItems = gameSate.availableMenuItems
                val menuItemHeight = 24
                val menuItemTopY = windowSize.height / 2 - availableMenuItems.size * menuItemHeight / 2
                availableMenuItems.forEachIndexed { index, menuItem ->
                    val menuItemY = menuItemTopY + index * menuItemHeight
                    if(menuItem == gameSate.selectedMenuItem) {
                        canvas.drawLine(
                            color = defaultColor,
                            point1 = Point(x = menuItemHeight / 2, y = menuItemY),
                            point2 = Point(x = menuItemHeight / 2, y = menuItemY + menuItemHeight)
                        )
                    }
                    canvas.drawText(
                        pointTopLeft = Point(
                            x = menuItemHeight.toFloat(),
                            y = menuItemY + (menuItemHeight - defaultFontHeight)/2
                        ),
                        text = menuItem.name
                    )
                }
            }
            is PingpongGameState.Game -> TODO()
        }
    }
}
