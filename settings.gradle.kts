pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://sagebionetworks.jfrog.io/artifactory/mobile-sdks/")
    }
}
rootProject.name = "MotorControl-Android"
include(":app")
include(":MotorControl")
