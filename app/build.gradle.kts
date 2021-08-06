import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.android.build.gradle.internal.lint.AndroidLintTask
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import io.gitlab.arturbosch.detekt.Detekt
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import java.util.TimeZone

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("kotlin-parcelize")
  id("com.google.protobuf")
  id("org.jmailen.kotlinter")
  id("io.gitlab.arturbosch.detekt")
  id("idea")
  id("jacoco")
  id("com.google.firebase.firebase-perf")
}

object KeyLoader {
  const val KEYSTORE_PATH = "keystore"
  private const val KEYS = "keys"
  const val STORE_PASS = "storePass"
  const val KEY_ALIAS = "keyAlias"
  const val KEY_PASS = "keyPass"

  private fun Properties.default() {
    this.setProperty(KEYSTORE_PATH, "placeholder")
    this.setProperty(STORE_PASS, "placeholder")
    this.setProperty(KEY_ALIAS, "placeholder")
    this.setProperty(KEY_PASS, "placeholder")
  }

  private val signing = File("signing.properties")

  private val properties by lazy {
    Properties().apply {
      if (signing.exists()) {
        val signProps = Properties().apply { load(FileInputStream(signing)) }
        val keyFile = File(signProps.getProperty(KEYS))
        if (keyFile.exists()) {
          load(FileInputStream(keyFile))
        } else {
          default()
        }
      } else {
        default()
      }
    }
  }

  fun getValue(key: String): String = properties.getProperty(key)
}

fun String.runCommand(currentWorkingDir: File = file("./")): String {
  val byteOut = ByteArrayOutputStream()
  project.exec {
    workingDir = currentWorkingDir
    commandLine = this@runCommand.split("\\s".toRegex())
    standardOutput = byteOut
  }
  return String(byteOut.toByteArray()).trim()
}


fun buildTime(): String {
  val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
  df.timeZone = TimeZone.getTimeZone("UTC")
  return df.format(Date())
}

fun gitHash(): String {
  return "git -C $rootDir rev-parse --short HEAD".runCommand()
}

val version = "1.5.1"
val code = 124

android {
  compileSdk = 30
  buildToolsVersion = "30.0.3"

  buildFeatures {
    viewBinding = true
    compose = true
  }

  defaultConfig {
    applicationId = "com.kelsos.mbrc"
    minSdk = 23
    targetSdk = 30
    versionCode = code
    versionName = version
    buildConfigField("String", "GIT_SHA", "\"${gitHash()}\"")
    buildConfigField("String", "BUILD_TIME", "\"${buildTime()}\"")

    kapt {
      arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
      }
    }
  }

  testOptions {
    unitTests.isReturnDefaultValues = true
    unitTests.isIncludeAndroidResources = true
    execution = "ANDROIDX_TEST_ORCHESTRATOR"
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs = listOf(
      "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
      "-Xopt-in=kotlin.RequiresOptIn",
      "-Xopt-in=kotlin.time.ExperimentalTime",
    )
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.get()
  }

  signingConfigs {
    create("release") {
      storeFile = file(KeyLoader.getValue(KeyLoader.KEYSTORE_PATH))
      keyAlias = KeyLoader.getValue(KeyLoader.KEY_ALIAS)
      storePassword = KeyLoader.getValue(KeyLoader.STORE_PASS)
      keyPassword = KeyLoader.getValue(KeyLoader.KEY_PASS)
    }
  }

  buildTypes {
    getByName("release") {
      signingConfig = signingConfigs.getByName("release")
      isDebuggable = false
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

      buildConfigField("String", "GIT_SHA", "\"${gitHash()}\"")
      buildConfigField("String", "BUILD_TIME", "\"${buildTime()}\"")
    }

    getByName("debug") {
      applicationIdSuffix = ".dev"
      versionNameSuffix = "-dev"
      isTestCoverageEnabled = true

      buildConfigField("String", "GIT_SHA", "\"debug_build\"")
      buildConfigField("String", "BUILD_TIME", "\"debug_build\"")
    }
  }

  flavorDimensions.add("base")

  productFlavors {
    create("play") {
      apply(plugin = "com.google.gms.google-services")
      apply(plugin = "com.google.firebase.crashlytics")
      apply(plugin = "com.google.firebase.firebase-perf")
    }

    create("github") {}
  }

  packagingOptions {
    resources {
      excludes += "META-INF/ASL2.0"
      excludes += "META-INF/LICENSE"
      excludes += "META-INF/NOTICE"
      excludes += "META-INF/services/javax.annotation.processing.Processor"
      excludes += "**/module-info.class"
      pickFirsts.add("META-INF/atomicfu.kotlin_module")
    }
  }

  lint {
    isWarningsAsErrors = true
    lintConfig = rootProject.file("config/lint.xml")
    sarifReport = true
  }

  applicationVariants.all {
    val variant = this
    variant.outputs.map { it as BaseVariantOutputImpl }
      .forEach { output ->
        val applicationId = defaultConfig.applicationId
        val flavorName = variant.flavorName
        val name = "$applicationId-$flavorName-${variant.versionCode}-v${variant.versionName}.apk"
        output.outputFileName = name
      }
  }
}

detekt {
  input = files("src/main/java", "src/main/kotlin")
  config = rootProject.files("config/detekt/detekt.yml")
  buildUponDefaultConfig = true
  reports {
    sarif {
      enabled = true
    }
  }
}

val dummyGoogleServicesJson: Configuration by configurations.creating {
  isCanBeResolved = true
  isCanBeConsumed = false

  attributes {
    attribute(Attribute.of("google.services.json", String::class.java), "dummy-json")
  }
}

idea {
  module {
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

dependencies {
  coreLibraryDesugaring(libs.com.android.tools.desugar)

  implementation(projects.changelog)

  testImplementation(libs.androidx.arch.core.testing)
  testImplementation(libs.androidx.room.testing)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.test.runner)
  testImplementation(libs.androidx.test.junit)
  testImplementation(libs.androidx.test.truth)
  testImplementation(libs.bundles.androidx.test.espresso)
  testImplementation(libs.cash.app.turbine)
  testImplementation(libs.koin.test)
  testImplementation(libs.kotlin.coroutines.test)
  testImplementation(libs.mockk)
  testImplementation(libs.robolectric)

  implementation(libs.arrowKt)
  implementation(libs.androidx.activity.activityCompose)
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.datastore)
  implementation(libs.androidx.fragment.ktx)
  implementation(libs.androidx.navigation.navigationCompose)
  implementation(libs.androidx.media)
  implementation(libs.androidx.paging.runtime.ktx)
  implementation(libs.androidx.preference.ktx)
  implementation(libs.androidx.recyclerview)
  implementation(libs.androidx.swiperefresh)
  implementation(libs.androidx.viewpager2)
  implementation(libs.androidx.work.ktx)
  implementation(libs.bundles.androidx.compose)
  implementation(libs.bundles.androidx.lifecycle)
  implementation(libs.bundles.androidx.navigation)
  implementation(libs.bundles.androidx.room)
  implementation(libs.bundles.coil)
  implementation(libs.bundles.coroutines)
  implementation(libs.bundles.google.accompanist)
  implementation(libs.bundles.koin)
  implementation(libs.google.material)
  implementation(libs.google.protobuf.javalite)
  implementation(libs.squareup.moshi.lib)
  implementation(libs.squareup.okio)
  implementation(libs.squareup.picasso)
  implementation(libs.timber)

  kapt(libs.androidx.room.compiler)
  kapt(libs.squareup.moshi.codegen)

  debugImplementation(libs.androidx.fragment.testing)
  debugImplementation(libs.squareup.leakcanary)

  "playImplementation"(platform(libs.google.firebase.bom))
  "playImplementation"(libs.bundles.google.firebase)
  dummyGoogleServicesJson(projects.mbrc)
}

protobuf {
  protoc {
    artifact = "com.google.protobuf:protoc:${project.libs.versions.protobuf.get()}"
  }

  generateProtoTasks {
    all().forEach { task ->
      task.plugins {
        create("java") {
          option("lite")
        }
      }
    }
  }
}

open class GenerateGoogleServicesJson : DefaultTask() {

  @get:InputFiles
  var configuration by project.objects.property<Configuration>()

  @get:OutputFile
  var outputJson by project.objects.property<File>()

  @TaskAction
  fun generateJson() {
    outputJson.writeText(configuration.resolve().single().readText())
  }
}

jacoco {
  toolVersion = "0.8.7"
}

tasks {
  withType<Detekt> {
    jvmTarget = "1.8"
  }

  val lintReleaseSarifOutput =
    project.layout.buildDirectory.file("reports/sarif/lint-results-release.sarif")
  afterEvaluate {
    // Needs to be in afterEvaluate because it's not created yet otherwise
    named<AndroidLintTask>("lintGithubRelease") {
      sarifReportOutputFile.set(lintReleaseSarifOutput)
    }
  }

  val staticAnalysis by registering {
    val detektAll by named<Detekt>("detekt")
    val androidLintRelease = named<AndroidLintTask>("lintGithubRelease")

    dependsOn(detekt, detektAll, androidLintRelease, lintKotlin)
  }

  register<Sync>("collectSarifReports") {
    val detektAll by named<Detekt>("detekt")
    val androidLintRelease = named<AndroidLintTask>("lintGithubRelease")

    mustRunAfter(detekt, detektAll, androidLintRelease, lintKotlin, staticAnalysis)

    from(detektAll.sarifReportFile) {
      rename { "detekt-release.sarif" }
    }
    from(detekt.get().sarifReportFile) {
      rename { "detekt.sarif" }
    }
    from(lintReleaseSarifOutput) {
      rename { "android-lint.sarif" }
    }

    into("$buildDir/reports/sarif")

    doLast {
      logger.info("Copied ${inputs.files.files.filter { it.exists() }} into ${outputs.files.files}")
      logger.info(
        "Output dir contents:\n${
          outputs.files.files.first().listFiles()?.joinToString()
        }"
      )
    }
  }

  val copyDummyGoogleServicesJson by registering(GenerateGoogleServicesJson::class) {
    onlyIf { System.getenv("CI") == "true" }
    configuration = dummyGoogleServicesJson
    outputJson = file("google-services.json")
  }

  val checkGoogleServicesJson by registering {
    onlyIf { System.getenv("CI") != "true" }
    doLast {
      if (!project.file("google-services.json").exists()) {
        throw GradleException(
          "You need a google-services.json file to run this project. Please refer to the CONTRIBUTING.md file for details."
        )
      }
    }
  }

  afterEvaluate {
    named("processGithubReleaseGoogleServices")
      .dependsOn(copyDummyGoogleServicesJson, checkGoogleServicesJson)
    named("processPlayReleaseGoogleServices")
      .dependsOn(copyDummyGoogleServicesJson, checkGoogleServicesJson)
    named("processGithubDebugGoogleServices")
      .dependsOn(copyDummyGoogleServicesJson, checkGoogleServicesJson)
    named("processPlayDebugGoogleServices")
      .dependsOn(copyDummyGoogleServicesJson, checkGoogleServicesJson)
  }

  withType<Test> {
    configure<JacocoTaskExtension> {
      isIncludeNoLocationClasses = true
      excludes = listOf("jdk.internal.*")
    }
  }

  register<JacocoReport>("jacocoTestReport") {
    group = "reporting"
    description = "Generate jacoco coverage reports"
    dependsOn("testGithubDebugUnitTest")
    reports {
      xml.required.set(true)
      html.required.set(true)
    }

    val fileFilter = listOf(
      "**/R.class",
      "**/R$*.class",
      "**/BuildConfig.*",
      "**/Manifest*.*",
      "**/*Test*.*",
      "**/**_Impl**",
      "**/*$*$*.*", // Anonymous classes generated by kotlin
      "android/**/*.*",
      "androidx/**/*.*",
      "**/databinding/**/*.class",
      "**/**/*JsonAdapter.class",
      "**/DataBinderMapperImpl**",
      "**/DataBindingInfo**",
      "**/BR.*",
      "**/*\$inlined$*.*", // Kotlin specific, Jacoco can not handle several "$" in class name.
    )

    val classDirectoriesTree = fileTree(project.buildDir) {
      include(
        "**/intermediates/javac/githubDebug/compileGithubDebugJavaWithJavac/**",
        "**/tmp/kotlin-classes/githubDebug/**",
        "**/classes/**/main/**",
        "**/intermediates/classes/githubDebug/**",
        "**/intermediates/javac/githubDebug/*/classes/**",
      )
      exclude(fileFilter)
    }

    val sourceDirectoriesTree = fileTree(project.projectDir) {
      include(
        "src/main/java/**",
        "src/main/kotlin/**",
        "src/debug/java/**",
        "src/debug/kotlin/**"
      )
    }

    val executionDataTree = fileTree(project.buildDir) {
      include(
        "**/*.ec",
        "**/*.exec",
      )
    }

    sourceDirectories.setFrom(sourceDirectoriesTree)
    classDirectories.setFrom(classDirectoriesTree)
    executionData.setFrom(executionDataTree)
  }
}

configurations.all {
  resolutionStrategy {
    force("com.google.code.findbugs:jsr305:3.0.2")
    force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${project.libs.versions.kotlin.get()}")
    force("org.jetbrains.kotlin:kotlin-reflect:${project.libs.versions.kotlin.get()}")
  }
}
