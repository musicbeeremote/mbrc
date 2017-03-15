package com.kelsos.mbrc.ui.navigation.nowplaying

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.data.NowPlayingMoveRequest
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.NowPlayingRepository
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Scheduler
import io.reactivex.Single
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

  override fun reload(scrollToTrack: Boolean) {
    view?.showLoading()
    addDisposable(repository.getAndSaveRemote()
        .compose { schedule(it) }
        .subscribe({
          view?.update(it)
          view?.trackChanged(model.trackInfo, scrollToTrack)
          view?.hideLoading()
        }) {
          view?.failure(it)
          view?.hideLoading()
        })
  }

  override fun load() {
    addDisposable(repository.getAllCursor().compose { schedule(it) }
        .subscribe({
          view?.update(it)
          view?.trackChanged(model.trackInfo, true)
          view?.hideLoading()
        }) {
          view?.failure(it)
          view?.hideLoading()
        })
  }

  override fun search(query: String) {
    bus.post(MessageEvent.action(UserAction(Protocol.NowPlayingListSearch, query.trim { it <= ' ' })))
  }

  override fun moveTrack(from: Int, to: Int) {
    val data = NowPlayingMoveRequest(from, to)
    bus.post(MessageEvent.action(UserAction(Protocol.NowPlayingListMove, data)))
  }

  override fun play(position: Int) {
    bus.post(MessageEvent.action(UserAction(Protocol.NowPlayingListPlay, position)))
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
    bus.post(MessageEvent.action(UserAction(Protocol.NowPlayingListRemove, position)))
  }

  private fun schedule(it: Single<FlowCursorList<NowPlaying>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)
}
