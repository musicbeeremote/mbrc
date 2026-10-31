plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlinCompose)
}

android {
  namespace = "com.kelsos.mbrc.feature.settings"

  buildFeatures {
    compose = true
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

dependencies {
  implementation(project(":core:common"))
  implementation(project(":core:platform"))
  implementation(project(":core:ui"))
  implementation(project(":core:data"))
  implementation(project(":core:networking"))
  implementation(project(":core:queue"))

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.bundles.androidx.compose)
  implementation(libs.google.material)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.paging.compose)
  implementation(libs.androidx.paging.runtime.ktx)
  implementation(libs.androidx.appcompat)

  implementation(libs.koin.android)
  implementation(libs.koin.compose)

  implementation(libs.androidx.lifecycle.runtime.compose)

  implementation(libs.timber)
  implementation(libs.aboutlibraries.core)

  implementation(libs.squareup.okhttp)

  testImplementation(testFixtures(project(":core:common")))
  testImplementation(libs.androidx.arch.core.testing)
  testImplementation(libs.bundles.unit.test)
  testImplementation(libs.androidx.test.runner)
  testImplementation(libs.androidx.test.truth)
  testImplementation(libs.androidx.room.testing)
  testImplementation(libs.androidx.paging.testing)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
