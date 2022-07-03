plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlinAndroid.get().pluginId)
  alias(libs.plugins.kotlinter)
  alias(libs.plugins.detekt)
}

android {
  namespace = "com.kelsos.mbrc.changelog"
  compileSdk = 33

  defaultConfig {
    minSdk = 23
    targetSdk = 33

    consumerProguardFiles("consumer-rules.pro")
  }

  buildFeatures {
    viewBinding = true
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.google.material)
  implementation(libs.kotlin.stdlib)
}
