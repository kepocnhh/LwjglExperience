package lwjgl.game.pingpong

fun PingpongGameState.getAvailableMenuItems(): Set<PingpongGameState.MainMenu.Item> {
    val result = when(competition.status) {
        PingpongGameState.Competition.Status.STARTED -> TODO()
        PingpongGameState.Competition.Status.PAUSED -> TODO()
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
    return getAvailableMenuItems().firstOrNull {
        it.ordinal > mainMenu.selectedMenuItem.ordinal
    } ?: PingpongGameState.MainMenu.Item.values().first()
}

fun PingpongGameState.getPreviousAvailableMenuItem(): PingpongGameState.MainMenu.Item {
    return getAvailableMenuItems().reversed().firstOrNull {
        it.ordinal < mainMenu.selectedMenuItem.ordinal
    } ?: PingpongGameState.MainMenu.Item.values().last()
}
