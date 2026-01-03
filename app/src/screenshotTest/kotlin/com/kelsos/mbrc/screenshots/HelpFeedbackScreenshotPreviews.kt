package com.kelsos.mbrc.screenshots

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.kelsos.mbrc.core.ui.theme.RemoteTheme
import com.kelsos.mbrc.feature.misc.help.compose.EmptyFeedbackActions
import com.kelsos.mbrc.feature.misc.help.compose.FeedbackContentState
import com.kelsos.mbrc.feature.misc.help.compose.HelpFeedbackScreenContent

private val emptyFeedbackState = FeedbackContentState()

private val filledFeedbackState = FeedbackContentState(
  feedbackText = "I love this app! Great work on the remote control functionality.",
  includeDeviceInfo = true,
  includeLogInfo = false
)

private val allCheckedFeedbackState = FeedbackContentState(
  feedbackText = "I love this app! Great work on the remote control functionality.",
  includeDeviceInfo = true,
  includeLogInfo = true
)

@PreviewTest
@Preview(name = "Help Feedback Help Tab Light", showBackground = true)
@Composable
fun HelpFeedbackHelpTabPreviewLight() {
  RemoteTheme(darkTheme = false) {
    HelpFeedbackScreenContent(
      selectedTabIndex = 0,
      feedbackState = emptyFeedbackState,
      actions = EmptyFeedbackActions
    )
  }
}

@PreviewTest
@Preview(name = "Help Feedback Help Tab Dark", showBackground = true)
@Composable
fun HelpFeedbackHelpTabPreviewDark() {
  RemoteTheme(darkTheme = true) {
    HelpFeedbackScreenContent(
      selectedTabIndex = 0,
      feedbackState = emptyFeedbackState,
      actions = EmptyFeedbackActions
    )
  }
}

@PreviewTest
@Preview(name = "Help Feedback Feedback Tab Light", showBackground = true)
@Composable
fun HelpFeedbackFeedbackTabPreviewLight() {
  RemoteTheme(darkTheme = false) {
    HelpFeedbackScreenContent(
      selectedTabIndex = 1,
      feedbackState = emptyFeedbackState,
      actions = EmptyFeedbackActions
    )
  }
}

@PreviewTest
@Preview(name = "Help Feedback Feedback Tab Dark", showBackground = true)
@Composable
fun HelpFeedbackFeedbackTabPreviewDark() {
  RemoteTheme(darkTheme = true) {
    HelpFeedbackScreenContent(
      selectedTabIndex = 1,
      feedbackState = emptyFeedbackState,
      actions = EmptyFeedbackActions
    )
  }
}

@PreviewTest
@Preview(name = "Help Feedback With Text Light", showBackground = true)
@Composable
fun HelpFeedbackWithTextPreviewLight() {
  RemoteTheme(darkTheme = false) {
    HelpFeedbackScreenContent(
      selectedTabIndex = 1,
      feedbackState = filledFeedbackState,
      actions = EmptyFeedbackActions
    )
  }
}

@PreviewTest
@Preview(name = "Help Feedback With Text Dark", showBackground = true)
@Composable
fun HelpFeedbackWithTextPreviewDark() {
  RemoteTheme(darkTheme = true) {
    HelpFeedbackScreenContent(
      selectedTabIndex = 1,
      feedbackState = allCheckedFeedbackState,
      actions = EmptyFeedbackActions
    )
  }
}
