package com.kelsos.mbrc.features.library.sync

object SyncCategory {
  const val GENRES = 1
  const val ARTISTS = 2
  const val ALBUMS = 3
  const val TRACKS = 4
  const val PLAYLISTS = 5
}

typealias SyncProgress = suspend (current: Int, total: Int, category: Int) -> Unit
