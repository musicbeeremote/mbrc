package com.kelsos.mbrc.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import com.kelsos.mbrc.feature.settings.compose.UpdateRequiredScreen

@PreviewTest
@Preview(name = "Update Required Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun UpdateRequiredPreviewLight() {
  RemoteTheme(darkTheme = false) {
    UpdateRequiredScreen(
      version = "1.5.0",
      onDismiss = {}
    )
  }
}

@PreviewTest
@Preview(name = "Update Required Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun UpdateRequiredPreviewDark() {
  RemoteTheme(darkTheme = true) {
    UpdateRequiredScreen(
      version = "1.5.0",
      onDismiss = {}
    )
  }
}
