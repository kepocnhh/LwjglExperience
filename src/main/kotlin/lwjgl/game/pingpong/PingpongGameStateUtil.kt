package lwjgl.game.pingpong

fun PingpongGameState.getAvailableMenuItems(): Set<PingpongGameState.MainMenu.Item> {
    val result = when(competition.status) {
        PingpongGameState.Competition.Status.STARTED -> TODO()
        PingpongGameState.Competition.Status.PAUSED -> {
            setOf(
                PingpongGameState.MainMenu.Item.CONTINUE_GAME,
                PingpongGameState.MainMenu.Item.EXIT
            )
        }
        null -> {
            setOf(
                PingpongGameState.MainMenu.Item.START_NEW_GAME,
                PingpongGameState.MainMenu.Item.EXIT
            )
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
