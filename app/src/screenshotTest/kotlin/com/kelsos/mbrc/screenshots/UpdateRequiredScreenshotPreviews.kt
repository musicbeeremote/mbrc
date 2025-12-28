package com.kelsos.mbrc.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.features.settings.compose.UpdateRequiredScreen
import com.kelsos.mbrc.theme.RemoteTheme

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
