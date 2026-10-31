plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlinCompose)
}

android {
  namespace = "com.kelsos.mbrc.feature.minicontrol"

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(project(":core:common"))
  implementation(project(":core:ui"))
  implementation(project(":core:networking"))

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.bundles.androidx.compose)
  implementation(libs.google.material)

  implementation(libs.koin.android)
  implementation(libs.koin.compose)

  implementation(libs.coilKt.compose)
  implementation(libs.timber)

  testImplementation(testFixtures(project(":core:common")))
  testImplementation(libs.bundles.unit.test)
}
