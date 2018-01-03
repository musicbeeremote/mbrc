package com.kelsos.mbrc.ui.navigation.library.tracks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import com.kelsos.mbrc.content.library.tracks.TrackEntity
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import io.reactivex.rxkotlin.plusAssign
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
    disposables += bus.observe(LibraryRefreshCompleteEvent::class)
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .subscribe { load() }
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    disposables += repository.getAll()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .doFinally {
          view().hideLoading()
        }
        .subscribe({
          onTrackLoad(it)
        }, {
          view().failure(it)
          Timber.e(it, "Error while loading the data from the database")
        })
  }

  override fun reload() {
    disposables += repository.getAndSaveRemote()
        .observeOn(schedulerProvider.main())
        .subscribeOn(schedulerProvider.io())
        .doFinally {
          view().hideLoading()
        }
        .subscribe({
          onTrackLoad(it)
        }, {
          view().failure(it)
          Timber.e(it, "Error while loading the data from the database")
        })
  }

  private fun onTrackLoad(data: LiveData<List<TrackEntity>>) {
    if (::tracks.isInitialized) {
      tracks.removeObservers(this)
    }

    tracks = data
    tracks.observe(this, Observer {
      if (it != null) {
        view().update(it)
      }
    })
  }

}
