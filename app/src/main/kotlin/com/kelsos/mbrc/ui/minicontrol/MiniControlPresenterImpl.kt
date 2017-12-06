package com.kelsos.mbrc.ui.minicontrol

import com.kelsos.mbrc.content.activestatus.MainDataModel
import com.kelsos.mbrc.events.CoverChangedEvent
import com.kelsos.mbrc.events.PlayStateChange
import com.kelsos.mbrc.events.TrackInfoChangeEvent
import com.kelsos.mbrc.events.UserAction.Companion.create
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerNext
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPlayPause
import com.kelsos.mbrc.networking.protocol.Protocol.PlayerPrevious
import javax.inject.Inject

class MiniControlPresenterImpl
@Inject constructor(
  private val model: MainDataModel,
  private val bus: RxBus
) :
  BasePresenter<MiniControlView>(), MiniControlPresenter {

  override fun load() {
    if (!isAttached) {
      return
    }
    view().updateCover(model.coverPath)
    view().updateState(model.playState)
    view().updateTrackInfo(model.trackInfo)
  }

  override fun attach(view: MiniControlView) {
    super.attach(view)
    bus.register(this, CoverChangedEvent::class.java, { this.view().updateCover(it.path) }, true)
    bus.register(
      this,
      TrackInfoChangeEvent::class.java,
      { this.view().updateTrackInfo(it.trackInfo) },
      true
    )
    bus.register(this, PlayStateChange::class.java, { this.view().updateState(it.state) }, true)
  }

  override fun next() {
    post(PlayerNext)
  }

  override fun previous() {
    post(PlayerPrevious)
  }

  override fun playPause() {
    post(PlayerPlayPause)
  }

  fun post(action: String) {
    bus.post(create(action))
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }
}
