import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
  alias(libs.plugins.versionsBenManes)
  alias(libs.plugins.versionCatalogUpdate)
  alias(libs.plugins.kover)
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlinAndroid) apply false
  alias(libs.plugins.kotlinter) apply false
  alias(libs.plugins.kotlinParcelize) apply false
  alias(libs.plugins.detekt)
}

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}


allprojects {
  apply {
    plugin("io.gitlab.arturbosch.detekt")
  }

  buildscript {
    repositories {
      google()
      gradlePluginPortal()
      mavenCentral()
    }
  }

  repositories {
    google()
    mavenCentral()
  }
}

subprojects {
  pluginManager.withPlugin("org.jetbrains.kotlin.android") {
    apply(plugin = "org.jmailen.kotlinter")
    apply(plugin = "org.jetbrains.kotlinx.kover")
  }

  pluginManager.withPlugin("com.android.library") {
    configure<com.android.build.gradle.LibraryExtension> {
      lint {
        lintConfig = rootProject.file("config/lint.xml")
        sarifReport = true
      }
    }
  }
}


kover {}

val dummyGoogleServices: Configuration by configurations.creating {
  isCanBeConsumed = true
  isCanBeResolved = false

  attributes {
    attribute(Attribute.of("google.services.json", String::class.java), "dummy-json")
  }
}

dependencies {
  dummyGoogleServices(files(rootProject.file("config/dummy-google-services.json")))

  // Kover coverage aggregation - includes all modules for unified coverage reports
  kover(project(":app"))
  kover(project(":core:common"))
  kover(project(":core:data"))
  kover(project(":core:networking"))
  kover(project(":core:platform"))
  kover(project(":core:queue"))
  kover(project(":core:ui"))
  kover(project(":feature:content"))
  kover(project(":feature:library"))
  kover(project(":feature:minicontrol"))
  kover(project(":feature:misc"))
  kover(project(":feature:playback"))
  kover(project(":feature:settings"))
  kover(project(":feature:widgets"))
}

tasks.register("staticAnalysisAll") {
  description = "Runs detekt, lint, and kotlinter on all modules"
  dependsOn(subprojects.flatMap { it.tasks.matching { t -> t.name == "detekt" } })
  dependsOn(subprojects.flatMap { it.tasks.matching { t -> t.name == "lintKotlin" } })
  dependsOn(subprojects.flatMap { it.tasks.matching { t -> t.name == "lint" } })
}

abstract class CollectSarifReportsTask : DefaultTask() {

  @get:Input
  abstract val subprojectBuildDirs: ListProperty<File>

  @get:OutputDirectory
  abstract val reportsDir: DirectoryProperty

  @TaskAction
  fun execute() {
    val outputDir = reportsDir.get().asFile
    outputDir.mkdirs()

    val buildDirs = subprojectBuildDirs.get()

    // Collect and merge detekt SARIF reports
    val detektFiles = buildDirs.mapNotNull { buildDir ->
      val sarifFile = File(buildDir, "reports/detekt/detekt.sarif")
      if (sarifFile.exists()) sarifFile else null
    }
    if (detektFiles.isNotEmpty()) {
      mergeSarifFiles(detektFiles, File(outputDir, "detekt.sarif"))
      logger.lifecycle("Merged ${detektFiles.size} detekt reports into detekt.sarif")
    }

    // Collect and merge Android lint SARIF reports (by variant)
    val allLintFiles = mutableListOf<File>()
    listOf("debug", "release", "githubDebug", "githubRelease").forEach { variant ->
      val lintFiles = buildDirs.mapNotNull { buildDir ->
        val sarifFile = File(buildDir, "reports/lint-results-$variant.sarif")
        if (sarifFile.exists()) sarifFile else null
      }
      if (lintFiles.isNotEmpty()) {
        mergeSarifFiles(lintFiles, File(outputDir, "lint-$variant.sarif"))
        logger.lifecycle("Merged ${lintFiles.size} lint reports into lint-$variant.sarif")
        allLintFiles.addAll(lintFiles)
      }
    }

    // Create unified android-lint.sarif with all variants
    if (allLintFiles.isNotEmpty()) {
      mergeSarifFiles(allLintFiles, File(outputDir, "android-lint.sarif"))
      logger.lifecycle("Merged ${allLintFiles.size} total lint reports into android-lint.sarif")
    }

    val files = outputDir.listFiles()?.filter { it.extension == "sarif" } ?: emptyList()
    logger.lifecycle("Created ${files.size} merged SARIF reports in ${outputDir.absolutePath}")
    files.forEach { logger.lifecycle("  - ${it.name}") }
  }

  private fun mergeSarifFiles(inputFiles: List<File>, outputFile: File) {
    var schema: String? = null
    var version: String? = null
    var tool: Map<String, Any?>? = null
    val allResults = mutableListOf<Map<String, Any?>>()
    val allArtifacts = mutableListOf<Map<String, Any?>>()

    inputFiles.forEach { file ->
      @Suppress("UNCHECKED_CAST")
      val json = groovy.json.JsonSlurper().parse(file) as Map<String, Any?>
      schema = schema ?: json["\$schema"] as? String
      version = version ?: json["version"] as? String

      @Suppress("UNCHECKED_CAST")
      val runs = json["runs"] as? List<Map<String, Any?>> ?: emptyList()

      runs.forEach { run ->
        @Suppress("UNCHECKED_CAST")
        tool = tool ?: run["tool"] as? Map<String, Any?>

        val currentOffset = allArtifacts.size

        @Suppress("UNCHECKED_CAST")
        val artifacts = run["artifacts"] as? List<Map<String, Any?>> ?: emptyList()
        allArtifacts.addAll(artifacts)

        @Suppress("UNCHECKED_CAST")
        val results = run["results"] as? List<Map<String, Any?>> ?: emptyList()
        if (currentOffset > 0 && artifacts.isNotEmpty()) {
          results.forEach { result ->
            @Suppress("UNCHECKED_CAST")
            val locations = result["locations"] as? List<Map<String, Any?>>
            locations?.forEach { location ->
              @Suppress("UNCHECKED_CAST")
              val physicalLocation = location["physicalLocation"] as? MutableMap<String, Any?>
              @Suppress("UNCHECKED_CAST")
              val artifactIndex = physicalLocation?.get("artifactLocation") as? MutableMap<String, Any?>
              val index = artifactIndex?.get("index") as? Int
              if (index != null) {
                artifactIndex["index"] = index + currentOffset
              }
            }
          }
        }
        allResults.addAll(results)
      }
    }

    val mergedRun = mutableMapOf<String, Any?>(
      "tool" to tool,
      "results" to allResults
    )
    if (allArtifacts.isNotEmpty()) {
      mergedRun["artifacts"] = allArtifacts
    }

    val merged = mapOf(
      "\$schema" to schema,
      "version" to version,
      "runs" to listOf(mergedRun)
    )

    outputFile.writeText(groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(merged)))
  }
}

tasks.register<CollectSarifReportsTask>("collectSarifReports") {
  description = "Merges all SARIF reports from all modules into single files per tool"
  mustRunAfter("staticAnalysisAll")
  subprojectBuildDirs.set(subprojects.map { it.layout.buildDirectory.asFile.get() })
  reportsDir.set(layout.buildDirectory.dir("reports/sarif"))
}

tasks.register("testAll") {
  description = "Runs unit tests on all modules"
  dependsOn(subprojects.flatMap { it.tasks.matching { t -> t.name == "test" } })
}

tasks.register("verifyLocalAll") {
  description = "Runs all local verification checks on all modules"
  dependsOn("staticAnalysisAll")
  dependsOn("testAll")
  // App-specific tasks
  dependsOn(":app:validateGithubDebugScreenshotTest")
}

tasks.register("verifyAll") {
  description = "Runs all verification checks including instrumentation tests"
  dependsOn("verifyLocalAll")
  dependsOn(subprojects.flatMap { it.tasks.matching { t -> t.name == "connectedDebugAndroidTest" } })
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    // Don't upgrade from stable to non-stable versions
    (isNonStable(candidate.version) && !isNonStable(currentVersion)) ||
      // Keep Kotlin version on the version specified in libs.versions.toml
      (candidate.module == "kotlin-gradle-plugin" && candidate.version != libs.versions.kotlin.get()) ||
      // KSP versions are compound versions, starting with the kotlin version
      (candidate.group == "com.google.devtools.ksp" && !candidate.version.startsWith(libs.versions.kotlin.get()))
  }
}
