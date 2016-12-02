package com.kelsos.mbrc.ui.navigation.playlists.tracks

import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.domain.PlaylistTrack
import javax.inject.Inject


class PlaylistTrackPresenterImpl
@Inject constructor(): PlaylistTrackPresenter {
  private var view: PlaylistTrackView? = null

  override fun bind(view: PlaylistTrackView) {
    this.view = view
  }

  override fun load(longExtra: Long) {
    TODO()
  }

  override fun queue(track: PlaylistTrack, @Action action: String) {
    if (track.path.isNullOrEmpty()) {
      return
    }

    TODO()
  }
}
