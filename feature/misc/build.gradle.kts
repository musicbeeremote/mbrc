plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlinCompose)
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.kelsos.mbrc.feature.misc"

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

  implementation(libs.timber)
  implementation(libs.squareup.moshi.lib)
  implementation(libs.squareup.okio)
  ksp(libs.squareup.moshi.codegen)

  testImplementation(testFixtures(project(":core:common")))
  testImplementation(libs.bundles.unit.test)
  testImplementation(libs.androidx.test.runner)
}
