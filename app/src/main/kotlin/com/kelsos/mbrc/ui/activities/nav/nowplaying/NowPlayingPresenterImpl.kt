package com.kelsos.mbrc.ui.activities.nav.nowplaying

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.data.NowPlayingMoveRequest
import com.kelsos.mbrc.data.UserAction
import com.kelsos.mbrc.events.MessageEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.rx.RxUtils
import com.kelsos.mbrc.services.NowPlayingSync
import rx.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class NowPlayingPresenterImpl
@Inject constructor(private val sync: NowPlayingSync,
                    private val bus: RxBus,
                    private val model: MainDataModel) :
    BasePresenter<NowPlayingView>(),
    NowPlayingPresenter {

  override fun refresh() {
    addSubcription(sync.syncNowPlaying(Schedulers.io())
        .compose(RxUtils.uiTask())
        .doOnTerminate {
          view?.refreshingDone()
        }
        .subscribe({
          view?.reload()
          view?.trackChanged(model.trackInfo)
        }) { Timber.v(it, "Failed") })
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
}
