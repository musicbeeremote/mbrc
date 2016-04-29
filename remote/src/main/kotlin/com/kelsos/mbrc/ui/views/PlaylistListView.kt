package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.Playlist

interface PlaylistListView {
  fun update(playlists: List<Playlist>)
}
