plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.kotlinCompose)
}

android {
  namespace = "com.kelsos.mbrc.core.ui"
  compileSdk = 36

  defaultConfig {
    minSdk = 23
  }

  buildFeatures {
    compose = true
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
}

dependencies {
  implementation(project(":core:common"))

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.bundles.androidx.compose)
  implementation(libs.google.material)
  implementation(libs.androidx.paging.compose)
}
