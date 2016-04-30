package com.kelsos.mbrc.presenters

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.ui.views.AlbumTrackView

interface AlbumTracksPresenter {
  fun bind(view: AlbumTrackView)

  fun load(albumId: Long)

  fun play(albumId: Long)

  fun queue(entry: Track, @Queue.Action action: String)
}
