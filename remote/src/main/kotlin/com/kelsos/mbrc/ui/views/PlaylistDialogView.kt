package com.kelsos.mbrc.ui.views

import com.kelsos.mbrc.domain.Playlist

interface PlaylistDialogView {
  fun update(playlists: List<Playlist>)
}
