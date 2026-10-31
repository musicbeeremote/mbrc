plugins {
  alias(libs.plugins.android.test)
  alias(libs.plugins.baselineprofile)
}

android {
  namespace = "com.kelsos.mbrc.baselineprofile"

  defaultConfig {
    minSdk = 28
    targetSdk = providers.gradleProperty("mbrc.sdk.target").get().toInt()
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  targetProjectPath = ":app"

  flavorDimensions += "base"
  productFlavors {
    create("play") {}
    create("github") {}
  }
}

baselineProfile {
  useConnectedDevices = true
}

dependencies {
  implementation(libs.androidx.test.junit)
  implementation(libs.androidx.test.espresso.core)
  implementation(libs.androidx.benchmark.macro.junit4)
}
