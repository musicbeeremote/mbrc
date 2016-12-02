package com.kelsos.mbrc.ui.navigation.nowplaying

import com.kelsos.mbrc.data.NowPlayingMoveRequest
import com.kelsos.mbrc.data.dao.NowPlaying
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.playlists.NowPlayingRepository
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Scheduler
import rx.Single
import javax.inject.Inject
import javax.inject.Named

class NowPlayingPresenterImpl
@Inject constructor(private val repository: NowPlayingRepository,
                    private val bus: RxBus,
                    private val model: MainDataModel,
                    @Named("io") private val ioScheduler: Scheduler,
                    @Named("main") private val mainScheduler: Scheduler) :
    BasePresenter<NowPlayingView>(),
    NowPlayingPresenter {

  override fun reload() {
    addSubcription(repository.getAndSaveRemote()
        .compose { schedule(it) }
        .subscribe({
          view?.update(it)
          view?.trackChanged(model.trackInfo)
        }) {
          view?.failure(it)
        })
  }

  override fun load() {
    addSubcription(repository.getAllCursor().compose { schedule(it) }
        .subscribe({
          view?.update(it)
          view?.trackChanged(model.trackInfo)
        }) {
          view?.failure(it)
        })
  }

  override fun search(query: String) {
    //todo search should not close and narrow down the the result set
  }

  override fun moveTrack(from: Int, to: Int) {
    val data = NowPlayingMoveRequest(from, to)
    //todo move
  }

  override fun play(position: Int) {
    //todo play
  }

  override fun attach(view: NowPlayingView) {
    super.attach(view)
    bus.register(this, TrackInfoChangeEvent::class.java, { this.view?.trackChanged(it.trackInfo) }, true)
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun removeTrack(position: Int) {
    // todo remove track
  }

  private fun schedule(it: Single<FlowCursorList<NowPlaying>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)
}
