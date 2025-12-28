package com.kelsos.mbrc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kelsos.mbrc.features.settings.compose.UpdateRequiredScreen
import com.kelsos.mbrc.theme.RemoteTheme

class UpdateRequiredActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val version = intent.getStringExtra(VERSION).orEmpty()

    setContent {
      RemoteTheme {
        UpdateRequiredScreen(
          version = version,
          onDismiss = { finish() }
        )
      }
    }
  }

  companion object {
    const val VERSION: String = "version"
  }
}
