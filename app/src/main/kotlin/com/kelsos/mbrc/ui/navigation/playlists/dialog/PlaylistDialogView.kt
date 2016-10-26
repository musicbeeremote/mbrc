package com.kelsos.mbrc.ui.navigation.playlists.dialog

import com.kelsos.mbrc.domain.Playlist

interface PlaylistDialogView {
  fun update(playlists: List<Playlist>)
}
