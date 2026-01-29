plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlinAndroid)
}

android {
  namespace = "com.kelsos.mbrc.core.common"
  compileSdk = 36

  defaultConfig {
    minSdk = 23
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
}

dependencies {
  implementation(libs.bundles.coroutines)
  implementation(libs.timber)
  implementation(libs.androidx.annotation)
  api(libs.androidx.lifecycle.viewmodel.ktx)

  // Unit test dependencies
  testImplementation(libs.truth)
  testImplementation(libs.androidx.test.junit)

  // Test fixtures dependencies
  testFixturesImplementation(libs.kotlin.coroutines.test)
  testFixturesImplementation(libs.koin.test)
  testFixturesImplementation(libs.squareup.moshi.lib)
}
