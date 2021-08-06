import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
  dependencies {
    val libs = project.extensions.getByType<VersionCatalogsExtension>()
      .named("libs") as org.gradle.accessors.dm.LibrariesForLibs
    classpath(libs.gradlePlugin.kotlin)
    classpath(libs.gradlePlugin.android)
    classpath(libs.gradlePlugin.crashlytics)
    classpath(libs.gradlePlugin.detekt)
    classpath(libs.gradlePlugin.gms)
    classpath(libs.gradlePlugin.performance)
    classpath(libs.gradlePlugin.kotlinter)
    classpath(libs.gradlePlugin.protobuf)
    classpath(libs.gradlePlugin.versionsBenManes)
    classpath(libs.jacoco)
  }

  repositories {
    google()
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
}

apply(plugin = "com.github.ben-manes.versions")
apply(plugin = "io.gitlab.arturbosch.detekt")
apply(plugin = "org.jmailen.kotlinter")

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

// ReleaseType/DependencyUpdates are copied from:
// https://github.com/chrisbanes/tivi/blob/main/buildSrc/src/main/java/app/tivi/buildsrc/DependencyUpdates.kt
// Check header for license
enum class ReleaseType(private val level: Int) {
  SNAPSHOT(0),
  DEV(1),
  ALPHA(10),
  BETA(20),
  RC(60),
  RELEASE(100);

  fun isEqualOrMoreStableThan(other: ReleaseType): Boolean = level >= other.level

  fun isLessStableThan(other: ReleaseType): Boolean = level < other.level
}

object DependencyUpdates {
  private val stableKeywords = arrayOf("RELEASE", "FINAL", "GA")
  private val releaseRegex = "^[0-9,.v-]+(-r)?$".toRegex(RegexOption.IGNORE_CASE)
  private val rcRegex = releaseKeywordRegex("rc")
  private val betaRegex = releaseKeywordRegex("beta")
  private val alphaRegex = releaseKeywordRegex("alpha")
  private val devRegex = releaseKeywordRegex("dev")

  fun versionToRelease(version: String): ReleaseType {
    val stableKeyword = stableKeywords.any { version.toUpperCase().contains(it) }
    if (stableKeyword) return ReleaseType.RELEASE

    return when {
      releaseRegex.matches(version) -> ReleaseType.RELEASE
      rcRegex.matches(version) -> ReleaseType.RC
      betaRegex.matches(version) -> ReleaseType.BETA
      alphaRegex.matches(version) -> ReleaseType.ALPHA
      devRegex.matches(version) -> ReleaseType.DEV
      else -> ReleaseType.SNAPSHOT
    }
  }

  private fun releaseKeywordRegex(keyword: String): Regex {
    return "^[0-9,.v-]+(-$keyword[0-9]*)$".toRegex(RegexOption.IGNORE_CASE)
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
    val current = DependencyUpdates.versionToRelease(currentVersion)

    if (current == ReleaseType.SNAPSHOT) {
      return@rejectVersionIf true
    }

    val candidate = DependencyUpdates.versionToRelease(candidate.version)
    return@rejectVersionIf candidate.isLessStableThan(current)
  }
}

val dummyGoogleServices: Configuration by configurations.creating {
  isCanBeConsumed = true
  isCanBeResolved = false

  attributes {
    attribute(Attribute.of("google.services.json", String::class.java), "dummy-json")
  }
}

dependencies {
  dummyGoogleServices(files(rootProject.file("config/dummy-google-services.json")))
}