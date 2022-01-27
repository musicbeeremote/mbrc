package com.kelsos.mbrc.features.playlists

import com.kelsos.mbrc.common.data.Mapper

object PlaylistEntityMapper :
  Mapper<PlaylistEntity, Playlist> {
  override fun map(from: PlaylistEntity): Playlist =
    Playlist(
      name = from.name,
      url = from.url,
      id = from.id,
    )
}

fun PlaylistEntity.toPlaylist(): Playlist = PlaylistEntityMapper.map(this)
