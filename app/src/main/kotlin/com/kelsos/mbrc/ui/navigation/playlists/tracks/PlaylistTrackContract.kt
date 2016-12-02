package com.kelsos.mbrc.ui.navigation.playlists.tracks

import com.kelsos.mbrc.annotations.Queue.Action
import com.kelsos.mbrc.domain.PlaylistTrack

interface PlaylistTrackPresenter {
  fun bind(view: PlaylistTrackView)

  fun load(longExtra: Long)

  fun queue(track: PlaylistTrack, @Action action: String)
}

interface PlaylistTrackView {

  fun showErrorWhileLoading()

  fun update(data: List<PlaylistTrack>)
}
