package com.kelsos.mbrc.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import com.kelsos.mbrc.feature.misc.whatsnew.ChangelogEntry
import com.kelsos.mbrc.feature.misc.whatsnew.EntryType
import com.kelsos.mbrc.feature.misc.whatsnew.WhatsNewScreen

private val sampleEntries = listOf(
  ChangelogEntry.Version(release = "2024-12-31", version = "1.6.0"),
  ChangelogEntry.Entry(
    text = "Complete UI rewrite using Jetpack Compose",
    type = EntryType.FEATURE
  ),
  ChangelogEntry.Entry(
    text = "New What's New screen to display changelog",
    type = EntryType.FEATURE
  ),
  ChangelogEntry.Entry(text = "Fixed connection state not updating properly", type = EntryType.BUG),
  ChangelogEntry.Entry(text = "Legacy View-based UI components", type = EntryType.REMOVED),
  ChangelogEntry.Version(release = "2024-06-15", version = "1.5.0"),
  ChangelogEntry.Entry(text = "Material 3 design language support", type = EntryType.FEATURE),
  ChangelogEntry.Entry(text = "Fixed crash when rotating device during sync", type = EntryType.BUG)
)

@PreviewTest
@Preview(name = "WhatsNew Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun WhatsNewScreenLightPreview() {
  RemoteTheme(darkTheme = false) {
    WhatsNewScreen(
      entries = sampleEntries,
      onDismiss = {}
    )
  }
}

@PreviewTest
@Preview(name = "WhatsNew Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun WhatsNewScreenDarkPreview() {
  RemoteTheme(darkTheme = true) {
    WhatsNewScreen(
      entries = sampleEntries,
      onDismiss = {}
    )
  }
}

@PreviewTest
@Preview(name = "WhatsNew Empty Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun WhatsNewScreenEmptyLightPreview() {
  RemoteTheme(darkTheme = false) {
    WhatsNewScreen(
      entries = emptyList(),
      onDismiss = {}
    )
  }
}

@PreviewTest
@Preview(name = "WhatsNew Empty Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun WhatsNewScreenEmptyDarkPreview() {
  RemoteTheme(darkTheme = true) {
    WhatsNewScreen(
      entries = emptyList(),
      onDismiss = {}
    )
  }
}

@PreviewTest
@Preview(name = "WhatsNew Loading Light", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun WhatsNewScreenLoadingLightPreview() {
  RemoteTheme(darkTheme = false) {
    WhatsNewScreen(
      entries = emptyList(),
      onDismiss = {},
      isLoading = true
    )
  }
}

@PreviewTest
@Preview(name = "WhatsNew Loading Dark", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun WhatsNewScreenLoadingDarkPreview() {
  RemoteTheme(darkTheme = true) {
    WhatsNewScreen(
      entries = emptyList(),
      onDismiss = {},
      isLoading = true
    )
  }
}
