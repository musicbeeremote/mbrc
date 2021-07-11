package com.kelsos.mbrc.ui.helpfeedback

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import com.kelsos.mbrc.logging.LogHelper
import com.kelsos.mbrc.ui.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class FeedbackViewModel(
  private val dispatchers: AppCoroutineDispatchers,
  private val logHelper: LogHelper
) : BaseViewModel<FeedbackUiMessage>() {

  fun zipLogs(filesDir: File, externalCacheDir: File?) {
    viewModelScope.launch(dispatchers.io) {
      try {
        val zipFile = logHelper.zipLogs(filesDir, externalCacheDir)
        emit(FeedbackUiMessage.ZipFileCreated(zipFile))
      } catch (e: Exception) {
        Timber.e(e, "Failed to create the zip file")
        emit(FeedbackUiMessage.SendFeedback)
      }
    }
  }

  fun checkForLogs(filesDir: File) {
    viewModelScope.launch(dispatchers.io) {
      val logsExist = logHelper.logsExist(filesDir)
      if (logsExist) {
        emit(FeedbackUiMessage.EnableLogs)
      }
    }
  }
}
