package com.kelsos.mbrc.ui.navigation.playlists

import com.kelsos.mbrc.domain.Playlist

interface PlaylistListView {
  fun update(playlists: List<Playlist>)
}
