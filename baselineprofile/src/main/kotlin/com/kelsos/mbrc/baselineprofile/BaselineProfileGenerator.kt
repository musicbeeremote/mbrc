package com.kelsos.mbrc.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a baseline profile for the app.
 *
 * Run this test on a device with API 28+ (rooted or userdebug build recommended for best results).
 *
 * To generate the profile:
 * ```
 * ./gradlew :app:generateGithubReleaseBaselineProfile
 * ```
 *
 * The generated profile will be placed in `app/src/main/baselineProfiles/`.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

  @get:Rule
  val rule = BaselineProfileRule()

  @Test
  fun generateBaselineProfile() {
    rule.collect(
      packageName = "com.kelsos.mbrc",
      includeInStartupProfile = true
    ) {
      // App startup journey
      pressHome()
      startActivityAndWait()

      // Wait for the app to settle after startup
      device.waitForIdle()
    }
  }
}
