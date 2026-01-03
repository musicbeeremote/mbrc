package com.kelsos.mbrc.feature.misc.help

import androidx.lifecycle.viewModelScope
import com.kelsos.mbrc.core.common.mvvm.BaseViewModel
import com.kelsos.mbrc.core.common.utilities.AppInfo
import com.kelsos.mbrc.core.common.utilities.logging.LogHelper
import java.io.File
import kotlinx.coroutines.launch

class FeedbackViewModel(private val logHelper: LogHelper, private val appInfo: AppInfo) :
  BaseViewModel<FeedbackUiMessage>() {

  val versionName: String get() = appInfo.versionName
  val applicationId: String get() = appInfo.applicationId
  fun checkIfLogsExist(filesDir: File) {
    viewModelScope.launch {
      val logsExist = logHelper.logsExist(filesDir = filesDir)
      emit(FeedbackUiMessage.UpdateLogsExist(logsExist))
    }
  }

  fun createZip(filesDir: File, externalCacheDir: File) {
    viewModelScope.launch {
      val result =
        runCatching {
          logHelper.zipLogs(filesDir, externalCacheDir)
        }

      val message =
        if (result.isSuccess) {
          FeedbackUiMessage.ZipSuccess(result.getOrThrow())
        } else {
          FeedbackUiMessage.ZipFailed()
        }

      emit(message)
    }
  }
}
