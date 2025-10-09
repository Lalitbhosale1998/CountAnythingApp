// C:/Users/lalit/Documents/App/settings.gradle.kts

pluginManagement {
    repositories {
        // Corrected block: No more restrictive content filter
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// This part is already correct and should be left as is.
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CountAnyThing"
include(":app")
