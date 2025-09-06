package com.kelsos.mbrc.screenshots

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.features.settings.CallAction
import com.kelsos.mbrc.features.settings.TrackAction
import com.kelsos.mbrc.features.settings.compose.BuildInfo
import com.kelsos.mbrc.features.settings.compose.EmptySettingsActions
import com.kelsos.mbrc.features.settings.compose.SettingsContentState
import com.kelsos.mbrc.features.settings.compose.SettingsScreenContent
import com.kelsos.mbrc.features.theme.Theme
import com.kelsos.mbrc.theme.RemoteTheme

private val previewBuildInfo = BuildInfo(
  versionName = "1.6.0",
  buildTime = "2025-01-01T12:00:00Z",
  gitRevision = "abc1234"
)

private val previewState = SettingsContentState(
  currentTheme = Theme.System,
  pluginUpdatesEnabled = true,
  debugLoggingEnabled = false,
  incomingCallAction = CallAction.None,
  trackDefaultAction = TrackAction.QueueNext,
  buildInfo = previewBuildInfo
)

@PreviewTest
@Preview(name = "Settings Screen Light", showBackground = true)
@Composable
fun SettingsScreenPreviewLight() {
  RemoteTheme(darkTheme = false) {
    Scaffold { padding ->
      SettingsScreenContent(
        state = previewState,
        actions = EmptySettingsActions
      )
    }
  }
}

@PreviewTest
@Preview(name = "Settings Screen Dark", showBackground = true)
@Composable
fun SettingsScreenPreviewDark() {
  RemoteTheme(darkTheme = true) {
    Scaffold { padding ->
      SettingsScreenContent(
        state = previewState,
        actions = EmptySettingsActions
      )
    }
  }
}
