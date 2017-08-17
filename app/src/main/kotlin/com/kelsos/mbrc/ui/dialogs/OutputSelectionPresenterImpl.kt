package com.kelsos.mbrc.ui.dialogs

import com.kelsos.mbrc.content.output.OutputApi
import com.kelsos.mbrc.mvp.BasePresenter
import io.reactivex.Scheduler
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Named

class OutputSelectionPresenterImpl
@Inject
constructor(
    private val outputApi: OutputApi,
    @Named("io") private val ioScheduler: Scheduler,
    @Named("main") private val mainScheduler: Scheduler
) : OutputSelectionPresenter, BasePresenter<OutputSelectionView>() {
  override fun load() {
    addDisposable(outputApi.getOutputs()
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .subscribe({
          view?.update(it)
        }) {
          view?.error(code(it))
        }
    )
  }

  override fun changeOutput(selectedOutput: String) {
    addDisposable(outputApi.setOutput(selectedOutput)
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .subscribe({
          view?.update(it)
        }) {
          view?.error(code(it))
        }
    )
  }

  @OutputSelectionContract.Code
  private fun code(throwable: Throwable?): Long {
    val cause = throwable?.cause ?: throwable

    return when (cause) {
      is SocketException -> OutputSelectionContract.CONNECTION_ERROR
      is SocketTimeoutException -> OutputSelectionContract.CONNECTION_ERROR
      else -> OutputSelectionContract.UNKNOWN_ERROR
    }
  }
}
