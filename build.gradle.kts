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
//        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
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
	    val currentOperatingSystem = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
	    when {
	        currentOperatingSystem.isMacOsX -> {
	        	jvmArgs = listOf("-XstartOnFirstThread")
	        }
	    }
    }
}

dependencies {
    implementation(Dependency.kotlinStdlib)

    val currentOperatingSystem = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
    val natives = when {
        currentOperatingSystem.isMacOsX -> "natives-macos"
        currentOperatingSystem.isWindows -> "natives-windows"
        else -> throw IllegalStateException()
    }
    "org.lwjgl".also { group ->
        implementation(platform("$group:lwjgl-bom:${Version.lwjgl}"))

        setOf(
            "lwjgl",
            "lwjgl-glfw",
            "lwjgl-opengl",
//            "lwjgl-vulkan",
            "lwjgl-stb"
        ).forEach { name ->
            implementation("$group:$name")
            runtimeOnly("$group:$name::$natives")
        }
    }
}
