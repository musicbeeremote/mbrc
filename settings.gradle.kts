pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }
}

rootProject.name = "mbrc"

include(":app")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
