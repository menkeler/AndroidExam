pluginManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
    plugins {
        id("com.android.application") version "7.0.0"
        id("com.android.library") version "7.0.0"
        // Add other plugin dependencies here if needed
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://jitpack.io")
    }
}

rootProject.name = "CaraguenoApp"
include(":app")


