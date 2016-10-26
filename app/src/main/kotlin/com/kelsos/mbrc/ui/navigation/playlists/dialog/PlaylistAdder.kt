package com.kelsos.mbrc.ui.navigation.playlists.dialog

interface PlaylistAdder {
  fun createPlaylist(selectionId: Long, name: String)

  fun playlistAdd(selectionId: Long, playlistId: Long)
}
