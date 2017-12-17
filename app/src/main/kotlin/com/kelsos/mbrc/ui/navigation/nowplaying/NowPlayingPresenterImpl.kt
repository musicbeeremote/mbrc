package com.kelsos.mbrc.ui.navigation.nowplaying

import android.arch.lifecycle.Observer
import com.kelsos.mbrc.content.activestatus.MainDataModel
import com.kelsos.mbrc.content.nowplaying.NowPlayingRepository
import com.kelsos.mbrc.events.TrackInfoChangeEvent
import com.kelsos.mbrc.events.UserAction
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.protocol.NowPlayingMoveRequest
import com.kelsos.mbrc.networking.protocol.Protocol
import com.kelsos.mbrc.utilities.SchedulerProvider
import com.kelsos.mbrc.utilities.paged
import javax.inject.Inject

class NowPlayingPresenterImpl
@Inject constructor(
    private val repository: NowPlayingRepository,
    private val bus: RxBus,
    private val model: MainDataModel,
    private val schedulerProvider: SchedulerProvider
) : BasePresenter<NowPlayingView>(),
    NowPlayingPresenter {

  override fun reload(scrollToTrack: Boolean) {
    view().showLoading()
    addDisposable(repository.getAndSaveRemote()
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          val liveData = it.paged()
          liveData.observe(this, Observer {
            if (it != null) {
              view().update(it)
            }
          })

          view().trackChanged(model.trackInfo, scrollToTrack)
          view().hideLoading()
        }) {
          view().failure(it)
          view().hideLoading()
        })
  }

  override fun load() {
    addDisposable(repository.getAll()
        .subscribeOn(schedulerProvider.io())
        .observeOn(schedulerProvider.main())
        .subscribe({
          val liveData = it.paged()
          liveData.observe(this, Observer {
            if (it != null) {
              view().update(it)
            }
          })
          view().trackChanged(model.trackInfo, true)
          view().hideLoading()
        }) {
          view().failure(it)
          view().hideLoading()
        })
  }

  override fun search(query: String) {
    // todo: drop and upgrade to do this locally, bus.post(UserAction(Protocol.NowPlayingListSearch, query.trim { it <= ' ' }))
  }

  override fun moveTrack(from: Int, to: Int) {
    val data = NowPlayingMoveRequest(from, to)
    bus.post(UserAction(Protocol.NowPlayingListMove, data))
  }

  override fun play(position: Int) {
    bus.post(UserAction(Protocol.NowPlayingListPlay, position))
  }

  override fun attach(view: NowPlayingView) {
    super.attach(view)
    bus.register(this, TrackInfoChangeEvent::class.java, { this.view().trackChanged(it.trackInfo) }, true)
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun removeTrack(position: Int) {
    bus.post(UserAction(Protocol.NowPlayingListRemove, position))
  }
}
