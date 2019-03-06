package com.kelsos.mbrc.content.playlists

import com.kelsos.mbrc.interfaces.data.Mapper

object PlaylistEntityMapper : Mapper<PlaylistEntity, Playlist> {
  override fun map(from: PlaylistEntity): Playlist {
    return Playlist(
      from.name,
      from.url,
      from.id
    )
  }
}

fun PlaylistEntity.toPlaylist(): Playlist {
  return PlaylistEntityMapper.map(this)
}
