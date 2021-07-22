import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
  dependencies {
    val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs") as org.gradle.accessors.dm.LibrariesForLibs
    classpath(libs.gradlePlugin.kotlin)
    classpath(libs.gradlePlugin.android)
    classpath(libs.gradlePlugin.crashlytics)
    classpath(libs.gradlePlugin.detekt)
    classpath(libs.gradlePlugin.gms)
    classpath(libs.gradlePlugin.safeArgs)
    classpath(libs.gradlePlugin.performance)
    classpath(libs.gradlePlugin.kotlinter)
    classpath(libs.gradlePlugin.protobuf)
    classpath(libs.gradlePlugin.versionsBenManes)
  }

  repositories {
    google()
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
}

apply(plugin="com.github.ben-manes.versions")
apply(plugin="io.gitlab.arturbosch.detekt")
apply(plugin="org.jmailen.kotlinter")

allprojects {
  buildscript {
    repositories {
      google()
      maven {
        url = uri("https://plugins.gradle.org/m2/")
      }
    }
  }

  repositories {
    google()
    mavenCentral()
  }
}

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    isNonStable(candidate.version)
  }
}

tasks {
  register("copyDummyDataForCi", Copy::class.java) {
    from(rootProject.file("config/dummy-google-services.json"))
    destinationDir = project(":app").projectDir
    rename { "google-services.json" }
  }
}