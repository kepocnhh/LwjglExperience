buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpathAll(Dependency.kotlinGradlePlugin)
    }
}

allprojects {
    repositories {
        jcenter()
    }
}

plugins {
    apply(Plugin.application)
    applyWithVersion(Plugin.kotlinJvm)
}

application {
    mainClassName = "lwjgl.experience.AppKt"
}

tasks.named<JavaExec>("run") {
    doFirst {
        jvmArgs = listOf("-XstartOnFirstThread")
    }
}

dependencies {
    implementation(Dependency.kotlinStdlib)

    val currentOperatingSystem = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
    val natives = when {
        currentOperatingSystem.isMacOsX -> "natives-macos"
        else -> throw IllegalStateException()
    }
    "org.lwjgl".also { group ->
        implementation(platform("$group:lwjgl-bom:${Version.lwjgl}"))

        setOf(
            "lwjgl",
            "lwjgl-glfw",
            "lwjgl-opengl",
            "lwjgl-stb"
        ).forEach { name ->
            implementation("$group:$name")
            runtimeOnly("$group:$name::$natives")
        }
    }
}
