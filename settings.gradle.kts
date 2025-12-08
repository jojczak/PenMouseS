pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        flatDir {
            dirs("libs")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "PenMouse S"

include(":app")

include(":core:ui")
include(":core:common")

include(":screen:home")
include(":screen:manual")
include(":screen:settings")

include(":mousemode:base")
include(":mousemode:basecursor")
include(":mousemode:mouse")
include(":mousemode:point")
include(":mousemode:scroll")
