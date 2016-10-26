package com.kelsos.mbrc.ui.navigation.library.album_tracks

import com.kelsos.mbrc.annotations.Queue
import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.ui.navigation.library.album_tracks.AlbumTrackView

interface AlbumTracksPresenter {
  fun bind(view: AlbumTrackView)

  fun load(albumId: Long)

  fun play(albumId: Long)

  fun queue(entry: Track, @Queue.Action action: String)
}
