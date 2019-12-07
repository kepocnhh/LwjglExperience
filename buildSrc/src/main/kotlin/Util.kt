import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.kotlin.dsl.version

fun DependencyHandler.implementation(dependency: Dependency) {
    add("implementation", dependency.notation())
}

fun DependencyHandler.implementationAll(
    firstDependency: Dependency,
    secondDependency: Dependency,
    vararg other: Dependency
) {
    implementation(firstDependency)
    implementation(secondDependency)
    other.forEach {
        implementation(it)
    }
}

private fun DependencyHandler.classpathDependency(dependency: Dependency) {
    add(ScriptHandler.CLASSPATH_CONFIGURATION, dependency.notation())
}

fun DependencyHandler.classpathAll(
    firstDependency: Dependency,
    vararg other: Dependency
) {
    classpathDependency(firstDependency)
    other.forEach {
        classpathDependency(it)
    }
}

fun PluginDependenciesSpec.apply(plugin: Plugin) {
    id(plugin.name)
}
fun PluginDependenciesSpec.applyWithVersion(plugin: Plugin) {
    id(plugin.name) version plugin.version
}

fun PluginDependenciesSpec.applyAll(firstPlugin: Plugin, secondPlugin: Plugin, vararg other: Plugin) {
    apply(firstPlugin)
    apply(secondPlugin)
    other.forEach {
        apply(it)
    }
}
fun PluginDependenciesSpec.applyAllWithVersion(firstPlugin: Plugin, secondPlugin: Plugin, vararg other: Plugin) {
    applyWithVersion(firstPlugin)
    applyWithVersion(secondPlugin)
    other.forEach {
        applyWithVersion(it)
    }
}
