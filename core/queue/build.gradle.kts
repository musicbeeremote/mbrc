plugins {
  alias(libs.plugins.android.library)
}

android {
  namespace = "com.kelsos.mbrc.core.queue"
}

dependencies {
  implementation(project(":core:common"))

  testImplementation(libs.truth)
  testImplementation(libs.androidx.test.junit)
}
