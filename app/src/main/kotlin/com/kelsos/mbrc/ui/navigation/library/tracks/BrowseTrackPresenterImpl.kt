package com.kelsos.mbrc.ui.navigation.library.tracks

import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class BrowseTrackPresenterImpl
@Inject constructor(
    private val bus: RxBus,
    private val repository: TrackRepository,
    private val schedulerProvider: SchedulerProvider
) :
    BasePresenter<BrowseTrackView>(),
    BrowseTrackPresenter {

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
    addDisposable(repository.getAllCursor().compose { schedule(it) }.subscribe({
      view().update(it)
      view().hideLoading()
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view().failure(it)
      view().hideLoading()
    }))
  }


  override fun reload() {
    view().showLoading()
    addDisposable(repository.getAndSaveRemote().compose { schedule(it) }.subscribe({
      view().update(it)
      view().hideLoading()
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view().failure(it)
      view().hideLoading()
    }))
  }

  private fun schedule(it: Single<FlowCursorList<Track>>) = it.observeOn(schedulerProvider.main())
      .subscribeOn(schedulerProvider.io())

}
