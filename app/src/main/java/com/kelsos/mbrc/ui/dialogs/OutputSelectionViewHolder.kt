package com.kelsos.mbrc.ui.dialogs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.utilities.AppRxSchedulers
import java.net.SocketException
import java.net.SocketTimeoutException

class OutputSelectionViewModel(
  private val outputApi: OutputApi,
  private val appRxSchedulers: AppRxSchedulers
) {

  private val outputs: MutableLiveData<List<String>> = MutableLiveData()

  fun getOutputs(): LiveData<List<String>> = outputs

  fun fetchOutputs() {
//    disposables += outputApi.getOutputs()
//      .subscribeOn(appRxSchedulers.disk)
//      .observeOn(appRxSchedulers.main)
//      .subscribe({
//        view().update(it)
//      }) {
//        view().error(code(it))
//      }
  }

  fun changeOutput(selectedOutput: String) {
//    disposables += outputApi.setOutput(selectedOutput)
//      .subscribeOn(appRxSchedulers.disk)
//      .observeOn(appRxSchedulers.main)
//      .subscribe({
//        view().update(it)
//      }) {
//        view().error(code(it))
//      }
  }

  @OutputSelectionContract.Code
  private fun code(throwable: Throwable?): Int {
    val cause = throwable?.cause ?: throwable

    return when (cause) {
      is SocketException -> OutputSelectionContract.CONNECTION_ERROR
      is SocketTimeoutException -> OutputSelectionContract.CONNECTION_ERROR
      else -> OutputSelectionContract.UNKNOWN_ERROR
    }
  }
}