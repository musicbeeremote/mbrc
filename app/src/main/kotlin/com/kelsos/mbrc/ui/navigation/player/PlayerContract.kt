package com.kelsos.mbrc.ui.navigation.player

import com.kelsos.mbrc.content.activestatus.PlayerStatusModel
import com.kelsos.mbrc.content.activestatus.PlayingPosition
import com.kelsos.mbrc.content.activestatus.TrackRating
import com.kelsos.mbrc.content.library.tracks.PlayingTrack
import com.kelsos.mbrc.mvp.BaseView
import com.kelsos.mbrc.mvp.Presenter

interface PlayerView : BaseView {

  fun updateTrackInfo(playingTrack: PlayingTrack)

  fun updateProgress(position: PlayingPosition)

  fun showChangeLog()

  fun notifyPluginOutOfDate()

  fun updateStatus(playerStatus: PlayerStatusModel)

  fun updateRating(rating: TrackRating)
}

interface PlayerPresenter : Presenter<PlayerView> {
  fun load()
  fun toggleScrobbling()
  fun seek(position: Int)
  fun play()
  fun previous()
  fun next()
  fun stop(): Boolean
  fun shuffle()
  fun repeat()
  fun favorite(): Boolean
}
