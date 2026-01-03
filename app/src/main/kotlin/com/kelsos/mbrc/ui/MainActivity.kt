package com.kelsos.mbrc.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.kelsos.mbrc.service.ServiceChecker
import org.koin.android.ext.android.inject

/**
 * Main entry point for the Compose-based MusicBee Remote app.
 * This replaces the traditional fragment-based navigation with Compose Navigation.
 */
class MainActivity : ComponentActivity() {

  private val serviceChecker: ServiceChecker by inject()

  override fun onCreate(savedInstanceState: Bundle?) {
    // Install the splash screen before calling super.onCreate()
    installSplashScreen()

    super.onCreate(savedInstanceState)

    // Enable edge-to-edge display with transparent system bars
    // Using auto() with transparent scrims for both light and dark themes
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.auto(
        lightScrim = Color.TRANSPARENT,
        darkScrim = Color.TRANSPARENT
      ),
      navigationBarStyle = SystemBarStyle.auto(
        lightScrim = Color.TRANSPARENT,
        darkScrim = Color.TRANSPARENT
      )
    )

    // Allow content to draw behind system bars
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // Start the remote service if not already running (same as BaseActivity)
    serviceChecker.startServiceIfNotRunning()

    setContent {
      RemoteApp()
    }
  }
}
