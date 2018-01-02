package com.kelsos.mbrc.ui.navigation.library.tracks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import timber.log.Timber
import javax.inject.Inject

class BrowseTrackPresenterImpl
@Inject
constructor(
    private val bus: RxBus,
    private val repository: TrackRepository,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<BrowseTrackView>(),
    BrowseTrackPresenter {

  private lateinit var tracks: LiveData<List<TrackEntity>>

  override fun attach(view: BrowseTrackView) {
    super.attach(view)
    bus.register(this, LibraryRefreshCompleteEvent::class.java, { load() })
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    view().showLoading()
    addDisposable(repository.getAll()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          onTrackLoad(it)
          view().hideLoading()
        }, {
          Timber.v(it, "Error while loading the data from the database")
          view().failure(it)
          view().hideLoading()
        }))
  }

  private fun onTrackLoad(it: LiveData<List<TrackEntity>>) {
    if (::tracks.isInitialized) {
      tracks.removeObservers(this)
    }

    tracks = it
    tracks.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

  override fun reload() {
    view().showLoading()
    addDisposable(repository.getAndSaveRemote()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe({
          onTrackLoad(it)
          view().hideLoading()
        }, {
          Timber.v(it, "Error while loading the data from the database")
          view().failure(it)
          view().hideLoading()
        }))
  }

}
