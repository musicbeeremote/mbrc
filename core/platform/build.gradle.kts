plugins {
  alias(libs.plugins.android.library)
}

android {
  namespace = "com.kelsos.mbrc.core.platform"
}

dependencies {
  implementation(project(":core:common"))

  implementation(libs.bundles.coroutines)
  implementation(libs.bundles.androidx.media3)
  implementation(libs.androidx.core.ktx)
  implementation(libs.timber)

  testImplementation(libs.truth)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.test.junit)
  testImplementation(libs.robolectric)
}
