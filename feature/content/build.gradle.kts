plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlinCompose)
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.kelsos.mbrc.feature.content"

  buildFeatures {
    compose = true
  }
}

dependencies {
  implementation(project(":core:common"))
  implementation(project(":core:ui"))
  implementation(project(":core:data"))
  implementation(project(":core:networking"))
  implementation(project(":core:queue"))
  implementation(project(":feature:minicontrol"))

  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.bundles.androidx.compose)
  implementation(libs.google.material)
  implementation(libs.androidx.paging.compose)
  implementation(libs.androidx.paging.runtime.ktx)

  implementation(libs.koin.android)
  implementation(libs.koin.compose)

  implementation(libs.timber)
  implementation(libs.squareup.moshi.lib)
  ksp(libs.squareup.moshi.codegen)

  testImplementation(testFixtures(project(":core:common")))
  testImplementation(project(":core:data"))
  testImplementation(libs.androidx.arch.core.testing)
  testImplementation(libs.bundles.unit.test)
  testImplementation(libs.androidx.test.runner)
  testImplementation(libs.androidx.test.truth)
  testImplementation(libs.androidx.paging.common.ktx)
  testImplementation(libs.androidx.paging.testing)
  testImplementation(libs.androidx.room.testing)
}
