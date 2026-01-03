package com.kelsos.mbrc

import android.app.ActivityManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.DisplayMetrics
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

/**
 * Test to verify all Koin module bindings are correctly configured.
 *
 * This test uses Koin's static verification to check that all dependencies
 * can be resolved without actually instantiating them. This catches missing
 * bindings at test time rather than runtime.
 *
 * When adding new dependencies:
 * 1. If you add Android framework classes, add them to extraTypes
 * 2. If you add new interfaces, ensure they have bind<>() declarations
 * 3. Run this test to verify everything resolves correctly
 */
class KoinModuleVerificationTest {
  @OptIn(KoinExperimentalAPI::class)
  @Test
  fun verifyAllModules() {
    appModule.verify(
      extraTypes = listOf(
        // Android framework types that can't be instantiated in unit tests
        Context::class,
        Application::class,
        Resources::class,
        AssetManager::class,
        DisplayMetrics::class,
        Configuration::class,
        SharedPreferences::class,
        ActivityManager::class,
        AudioManager::class,
        NotificationManager::class,
        NotificationManagerCompat::class,
        WifiManager::class,
        ConnectivityManager::class,
        WorkManager::class,
        // WorkManager-specific types for Workers
        WorkerParameters::class
      )
    )
  }
}
