package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.PlaylistTrack
import com.kelsos.mbrc.ui.views.PlaylistTrackView

interface PlaylistTrackPresenter {
  fun bind(view: PlaylistTrackView)

  fun load(longExtra: Long)

  fun queue(track: PlaylistTrack, @Queue.Action action: String)
}
