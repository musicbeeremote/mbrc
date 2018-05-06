package com.kelsos.mbrc.ui.dialogs

import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.AppRxSchedulers
import io.reactivex.rxkotlin.plusAssign
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.inject.Inject

class OutputSelectionPresenterImpl
@Inject
constructor(
  private val outputApi: OutputApi,
  private val appRxSchedulers: AppRxSchedulers
) : OutputSelectionPresenter, BasePresenter<OutputSelectionView>() {
  override fun load() {
    disposables += outputApi.getOutputs()
      .subscribeOn(appRxSchedulers.disk)
      .observeOn(appRxSchedulers.main)
      .subscribe({
        view().update(it)
      }) {
        view().error(code(it))
      }
  }

  override fun changeOutput(selectedOutput: String) {
    disposables += outputApi.setOutput(selectedOutput)
      .subscribeOn(appRxSchedulers.disk)
      .observeOn(appRxSchedulers.main)
      .subscribe({
        view().update(it)
      }) {
        view().error(code(it))
      }
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