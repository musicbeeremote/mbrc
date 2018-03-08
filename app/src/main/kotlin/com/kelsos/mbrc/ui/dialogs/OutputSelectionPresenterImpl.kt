package com.kelsos.mbrc.ui.dialogs

import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.inject.Inject

class OutputSelectionPresenterImpl
@Inject
constructor(
    private val outputApi: OutputApi,
    private val schedulerProvider: SchedulerProvider
) : OutputSelectionPresenter, BasePresenter<OutputSelectionView>() {
  override fun load() {
    addDisposable(outputApi.getOutputs()
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          view().update(it)
        }) {
          view().error(code(it))
        }
    )
  }

  override fun changeOutput(selectedOutput: String) {
    addDisposable(outputApi.setOutput(selectedOutput)
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          view().update(it)
        }) {
          view().error(code(it))
        }
    )
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
