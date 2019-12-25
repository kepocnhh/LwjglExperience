package lwjgl.game.pingpong

import lwjgl.entity.Point
import lwjgl.entity.Size
import lwjgl.entity.square

fun PingpongGameState.getAvailableMenuItems(): Set<PingpongGameState.MainMenu.Item> {
    val environment = competition.environment
    val result = when {
        environment == null -> {
            setOf(
                PingpongGameState.MainMenu.Item.START_NEW_GAME,
                PingpongGameState.MainMenu.Item.EXIT
            )
        }
        environment.isPaused -> {
            setOf(
                PingpongGameState.MainMenu.Item.CONTINUE_GAME,
                PingpongGameState.MainMenu.Item.EXIT
            )
        }
        else -> {
            TODO()
        }
    }
    return result.sortedBy {
        it.ordinal
    }.toSet()
}

fun PingpongGameState.getNextAvailableMenuItem(): PingpongGameState.MainMenu.Item {
    val items = getAvailableMenuItems()
    return items.firstOrNull {
        it.ordinal > mainMenu.selectedMenuItem.ordinal
    } ?: items.first()
}

fun PingpongGameState.getPreviousAvailableMenuItem(): PingpongGameState.MainMenu.Item {
    val items = getAvailableMenuItems()
    return items.reversed().firstOrNull {
        it.ordinal < mainMenu.selectedMenuItem.ordinal
    } ?: items.last()
}

fun getTableSize(pictureSize: Size): Size {
    if(pictureSize.width > pictureSize.height) {
        val height = pictureSize.height * 0.8
//            val width = height / 2 * 3
        val width = height
        return Size(
            width = width.toInt(),
            height = height.toInt()
        )
    } else {
        TODO()
    }
}

fun getTableTopLeft(pictureSize: Size, tableSize: Size): Point {
    return Point(
        x = (pictureSize.width - tableSize.width) / 2,
        y = (pictureSize.height - tableSize.height) / 2
    )
}

fun getPlayerRacketSize(): Size {
    return Size(
        width = 16,
        height = 64
    )
}

fun getBallSize(): Size {
    return square(16)
}
