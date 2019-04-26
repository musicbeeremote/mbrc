package com.kelsos.mbrc.features.widgets

import com.kelsos.mbrc.content.library.tracks.PlayingTrack

interface WidgetUpdater {
  fun updatePlayingTrack(track: PlayingTrack)
  fun updatePlayState(state: String)
  fun updateCover(path: String = "")

  companion object {
    const val COVER = "com.kelsos.mbrc.features.widgets.COVER"
    const val COVER_PATH = "com.kelsos.mbrc.features.widgets.COVER_PATH"
    const val STATE = "com.kelsos.mbrc.features.widgets.STATE"
    const val INFO = "com.kelsos.mbrc.features.widgets.INFO"
    const val TRACK_INFO = "com.kelsos.mbrc.features.widgets.TRACKINFO"
    const val PLAYER_STATE = "com.kelsos.mbrc.features.widgets.PLAYER_STATE"
  }
}