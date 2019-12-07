private const val jetbrainsGroup = "org.jetbrains"
private const val kotlinGroup = "$jetbrainsGroup.kotlin"

data class Dependency(
    val group: String,
    val name: String,
    val version: String
) {
    companion object {
        val kotlinGradlePlugin = Dependency(
            group = kotlinGroup,
            name = "kotlin-gradle-plugin",
            version = Version.kotlin
        )
        val kotlinStdlib = Dependency(
            group = kotlinGroup,
            name = "kotlin-stdlib",
            version = Version.kotlin
        )
    }
}

fun Dependency.notation(): String {
    return "$group:$name:$version"
}

data class Plugin(
    val name: String,
    val version: String
) {
    companion object {
        val application = Plugin(
            name = "org.gradle.application",
            version = ""
        )

        val kotlinJvm = Plugin(
            name = "$kotlinGroup.jvm",
            version = Version.kotlin
        )
    }
}
