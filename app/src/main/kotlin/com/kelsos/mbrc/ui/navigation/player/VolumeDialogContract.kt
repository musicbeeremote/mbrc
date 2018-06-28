package com.kelsos.mbrc.ui.navigation.player

import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface VolumeDialogPresenter : Presenter<VolumeView> {
  fun changeVolume(volume: Int)
  fun mute()
}

interface VolumeView : BaseView {
  fun update(playerStatus: PlayerStatusModel)
}
