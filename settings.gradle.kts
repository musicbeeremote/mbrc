pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }
}

rootProject.name = "MusicBee Remote"

include(":changelog")
include(":app")

enableFeaturePreview("VERSION_CATALOGS")
