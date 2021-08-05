plugins {
  id("com.android.library")
  kotlin("android")
}

android {
  compileSdk = 30
  buildToolsVersion = "30.0.3"

  defaultConfig {
    minSdk = 23
    targetSdk = 30

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
