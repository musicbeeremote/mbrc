package com.kelsos.mbrc.ui.navigation.radio

import com.kelsos.mbrc.content.nowplaying.queue.Queue.NOW
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import timber.log.Timber
import javax.inject.Inject

@RadioActivity.Presenter
class RadioPresenterImpl
@Inject constructor(
    private val radioRepository: RadioRepository,
    private val queueApi: QueueApi,
    private val schedulerProvider: SchedulerProvider
) :
    BasePresenter<RadioView>(),
    RadioPresenter {

  override fun load() {

    view().showLoading()
    addDisposable(radioRepository.cacheIsEmpty().flatMap {
      if (it) {
        return@flatMap radioRepository.getAndSaveRemote()
      } else {
        return@flatMap radioRepository.getAllCursor()
      }
    }.subscribeOn(schedulerProvider.io()).observeOn(schedulerProvider.main()).subscribe({
      view().hideLoading()
      view().update(it)
    }, {
      view().error(it)
      view().hideLoading()
      Timber.v(it, "Failed")
    }))
  }

  override fun refresh() {
    view().showLoading()
    addDisposable(radioRepository.getAndSaveRemote()
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          view().update(it)
          view().hideLoading()
        }, {
          view().error(it)
          view().hideLoading()
        }))
  }

  override fun play(path: String) {
    addDisposable(queueApi.queue(NOW, listOf(path))
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          view().radioPlaySuccessful()
        }, {
          view().radioPlayFailed(it)
        }))
  }
}
