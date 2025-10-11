pluginManagement {
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
