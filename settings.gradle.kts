pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }
}

rootProject.name = "mbrc"

include(":app")

// Core modules
include(":core:common")
include(":core:ui")
include(":core:data")
include(":core:networking")
include(":core:platform")
include(":core:queue")

// Baseline profile module
include(":baselineprofile")

// Feature modules
include(":feature:library")
include(":feature:playback")
include(":feature:widgets")
include(":feature:settings")
include(":feature:content")
include(":feature:misc")
include(":feature:minicontrol")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
