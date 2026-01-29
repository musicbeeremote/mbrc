plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.kelsos.mbrc.core.data"
  compileSdk = 36

  defaultConfig {
    minSdk = 23
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlin {
    compilerOptions {
      jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
  }

  testFixtures {
    enable = true
  }

  sourceSets {
    getByName("androidTest").assets.srcDirs("$projectDir/schemas")
  }
}

ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
  implementation(project(":core:common"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.bundles.androidx.room)
  implementation(libs.androidx.paging.runtime.ktx)
  implementation(libs.koin.android)
  implementation(libs.squareup.moshi.lib)
  implementation(libs.squareup.okio)
  implementation(libs.timber)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.runtime)

  ksp(libs.androidx.room.compiler)
  ksp(libs.squareup.moshi.codegen)

  androidTestImplementation(libs.androidx.room.testing)
  androidTestImplementation(libs.androidx.test.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.truth)
  androidTestImplementation(libs.kotlin.coroutines.test)

  // Test fixtures dependencies
  testFixturesImplementation(libs.koin.test)
  testFixturesImplementation(libs.androidx.test.core)
  testFixturesImplementation(libs.androidx.room.runtime)
}
