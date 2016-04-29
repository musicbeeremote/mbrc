package com.kelsos.mbrc.presenters

interface PlaylistAdder {
  fun createPlaylist(selectionId: Long, name: String)

  fun playlistAdd(selectionId: Long, playlistId: Long)
}
