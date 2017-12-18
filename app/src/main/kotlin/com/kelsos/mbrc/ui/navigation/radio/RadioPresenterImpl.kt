package com.kelsos.mbrc.ui.navigation.radio

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.kelsos.mbrc.content.nowplaying.queue.Queue.NOW
import com.kelsos.mbrc.content.nowplaying.queue.QueueApi
import com.kelsos.mbrc.content.radios.RadioRepository
import com.kelsos.mbrc.content.radios.RadioStationEntity
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.paged
import timber.log.Timber
import javax.inject.Inject

@RadioActivity.Presenter
class RadioPresenterImpl
@Inject
constructor(
    private val radioRepository: RadioRepository,
    private val queueApi: QueueApi,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<RadioView>(),
    RadioPresenter {

  private lateinit var radios: LiveData<PagedList<RadioStationEntity>>

  override fun load() {

    view().showLoading()
    addDisposable(radioRepository.getAll()
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          onRadiosLoaded(it)
          view().hideLoading()
        }, {
          view().error(it)
          view().hideLoading()
          Timber.v(it, "Failed")
        }))
  }

  private fun onRadiosLoaded(factory: DataSource.Factory<Int, RadioStationEntity>) {
    radios = factory.paged()
    radios.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun refresh() {
    view().showLoading()
    addDisposable(radioRepository.getAndSaveRemote()
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          onRadiosLoaded(it)
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
