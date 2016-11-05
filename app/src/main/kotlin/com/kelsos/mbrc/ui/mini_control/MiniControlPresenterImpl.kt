package com.kelsos.mbrc.ui.mini_control

import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.CoverChangedEvent
import com.kelsos.mbrc.events.ui.PlayStateChange
import com.kelsos.mbrc.events.ui.TrackInfoChangeEvent
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.mvp.BasePresenter
import javax.inject.Inject

class MiniControlPresenterImpl
@Inject constructor(private val model: MainDataModel,
                    private val bus: RxBus) :
    BasePresenter<MiniControlView>(), MiniControlPresenter {

  override fun load() {
    if (!isAttached) {
      return
    }
    view?.updateCover()
    view?.updateState(model.playState)
    view?.updateTrackInfo(model.trackInfo)
  }

  override fun attach(view: MiniControlView) {
    super.attach(view)
    bus.register(this, CoverChangedEvent::class.java, { this.view?.updateCover() }, true)
    bus.register(this, TrackInfoChangeEvent::class.java, { this.view?.updateTrackInfo(it.trackInfo) }, true)
    bus.register(this, PlayStateChange::class.java, { this.view?.updateState(it.state) }, true)
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

}
