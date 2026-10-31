plugins {
  alias(libs.plugins.android.library)
}

android {
  namespace = "com.kelsos.mbrc.core.common"

  testFixtures {
    enable = true
  }
}

dependencies {
  implementation(libs.bundles.coroutines)
  implementation(libs.timber)
  implementation(libs.androidx.annotation)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.runtime)
  api(libs.androidx.lifecycle.viewmodel.ktx)

  // Unit test dependencies
  testImplementation(libs.truth)
  testImplementation(libs.androidx.test.junit)

  // Test fixtures dependencies
  testFixturesApi(libs.androidx.test.junit)
  testFixturesImplementation(libs.kotlin.coroutines.test)
  testFixturesImplementation(libs.koin.test)
  testFixturesImplementation(libs.squareup.moshi.lib)
}
