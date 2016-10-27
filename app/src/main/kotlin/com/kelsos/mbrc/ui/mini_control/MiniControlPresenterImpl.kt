package com.kelsos.mbrc.ui.mini_control

import com.kelsos.mbrc.constants.Const
import com.kelsos.mbrc.model.MainDataModel
import com.kelsos.mbrc.mvp.BasePresenter
import javax.inject.Inject

class MiniControlPresenterImpl : BasePresenter<MiniControlView>(), MiniControlPresenter {

  @Inject lateinit var model: MainDataModel

  override fun load() {
    if (!isAttached) {
      return
    }

    view?.updateCover(Const.COVER_FILE)
    view?.updateState(model.playState)
    view?.updateTrackInfo(model.trackInfo)
  }

}
