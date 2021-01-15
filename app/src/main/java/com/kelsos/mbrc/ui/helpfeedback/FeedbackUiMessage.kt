package com.kelsos.mbrc.ui.helpfeedback

import com.kelsos.mbrc.ui.UiMessageBase
import java.io.File

sealed class FeedbackUiMessage : UiMessageBase {
  class ZipFileCreated(val zip: File) : FeedbackUiMessage()
  object SendFeedback : FeedbackUiMessage()
  object EnableLogs : FeedbackUiMessage()
}
