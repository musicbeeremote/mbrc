package com.kelsos.mbrc.events.ui

import com.kelsos.mbrc.data.Playlist

class PlaylistAvailable private constructor(val playlist: List<Playlist>)
{
  companion object {
    fun create(playlist: List<Playlist>): PlaylistAvailable {
      return PlaylistAvailable(playlist)
    }
  }
}
