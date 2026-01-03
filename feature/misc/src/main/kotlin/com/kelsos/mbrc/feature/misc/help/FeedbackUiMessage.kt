package com.kelsos.mbrc.feature.misc.help

import com.kelsos.mbrc.core.common.mvvm.UiMessageBase
import java.io.File

sealed class FeedbackUiMessage : UiMessageBase {
  class UpdateLogsExist(val logsExist: Boolean) : FeedbackUiMessage()

  class ZipSuccess(val zipFile: File) : FeedbackUiMessage()

  class ZipFailed : FeedbackUiMessage()
}
