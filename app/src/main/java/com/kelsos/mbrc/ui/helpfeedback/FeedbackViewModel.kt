package com.kelsos.mbrc.ui.helpfeedback

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.logging.LogHelper
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.launch
import java.io.File

class FeedbackViewModel(
  dispatchers: AppCoroutineDispatchers,
  private val logHelper: LogHelper
) : BaseViewModel<FeedbackUiMessage>(dispatchers) {

  fun zipLogs(filesDir: File, externalCacheDir: File?) {
    scope.launch {
      try {
        val zipFile = logHelper.zipLogs(filesDir, externalCacheDir)
        emit(FeedbackUiMessage.ZipFileCreated(zipFile))
      } catch (e: Exception) {
        emit(FeedbackUiMessage.SendFeedback)
      }
    }
  }

  fun checkForLogs(filesDir: File) {
    scope.launch {
      val logsExist = logHelper.logsExist(filesDir)
      if (logsExist) {
        emit(FeedbackUiMessage.EnableLogs)
      }
    }
  }
}
