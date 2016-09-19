package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.views.MiniControlView
import javax.inject.Inject

class MiniControlPresenter : BasePresenter<MiniControlView>() {

  @Inject lateinit var model: MainDataModel

  fun load() {
    if (!isAttached) {
      return
    }

    view.updateCover(model!!.cover)
    view.updateState(model!!.playState)
    view.updateTrackInfo(model!!.trackInfo)
  }

}
