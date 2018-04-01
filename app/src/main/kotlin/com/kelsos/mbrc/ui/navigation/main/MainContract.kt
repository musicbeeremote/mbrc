package com.kelsos.mbrc.ui.navigation.main

import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.TrackPositionData
import com.kelsos.mbrc.content.activestatus.TrackRatingModel
import com.kelsos.mbrc.content.library.tracks.PlayingTrackModel
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface MainView : BaseView {

  fun updateTrackInfo(info: PlayingTrackModel)

  fun updateConnection(status: Int)

  fun updateProgress(duration: TrackPositionData)

  fun showChangeLog()

  fun notifyPluginOutOfDate()

  fun showPluginUpdateRequired(minimumRequired: String)

  fun updateStatus(playerStatus: PlayerStatusModel)

  fun updateRating(rating: TrackRatingModel)
}

interface MainViewPresenter : Presenter<MainView> {
  fun requestNowPlayingPosition()
  fun toggleScrobbling()
  fun seek(position: Int)
  fun play()
  fun previous()
  fun next()
  fun stop(): Boolean
  fun mute()
  fun shuffle()
  fun repeat()
  fun changeVolume(value: Int)
  fun lfmLove(): Boolean
}
