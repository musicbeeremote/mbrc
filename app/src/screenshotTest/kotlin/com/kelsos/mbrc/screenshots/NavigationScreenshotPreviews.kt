package com.kelsos.mbrc.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.app.DrawerContent
import com.kelsos.mbrc.app.Screen
import com.kelsos.mbrc.common.state.ConnectionStatus
import com.kelsos.mbrc.theme.RemoteTheme

@PreviewTest
@Preview(name = "Drawer Light", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun DrawerPreviewLight() {
  RemoteTheme(darkTheme = false) {
    DrawerContent(
      currentRoute = Screen.Home.route,
      connectionStatus = ConnectionStatus.Connected,
      connectionName = "Living Room PC",
      versionName = "1.6.0",
      onConnectionToggle = { },
      onNavigate = { }
    )
  }
}

@PreviewTest
@Preview(name = "Drawer Dark", showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun DrawerPreviewDark() {
  RemoteTheme(darkTheme = true) {
    DrawerContent(
      currentRoute = Screen.Home.route,
      connectionStatus = ConnectionStatus.Connected,
      connectionName = "Living Room PC",
      versionName = "1.6.0",
      onConnectionToggle = { },
      onNavigate = { }
    )
  }
}

@PreviewTest
@Preview(name = "Drawer Offline", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun DrawerPreviewOffline() {
  RemoteTheme(darkTheme = false) {
    DrawerContent(
      currentRoute = Screen.Home.route,
      connectionStatus = ConnectionStatus.Offline,
      connectionName = null,
      versionName = "1.6.0",
      onConnectionToggle = { },
      onNavigate = { }
    )
  }
}

@PreviewTest
@Preview(name = "Drawer Queue Selected", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun DrawerPreviewQueueSelected() {
  RemoteTheme(darkTheme = false) {
    DrawerContent(
      currentRoute = Screen.NowPlayingList.route,
      connectionStatus = ConnectionStatus.Connected,
      connectionName = "Desktop",
      versionName = "1.6.0",
      onConnectionToggle = { },
      onNavigate = { }
    )
  }
}
