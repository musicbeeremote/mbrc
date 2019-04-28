package com.kelsos.mbrc.features.playlists

import com.kelsos.mbrc.features.playlists.data.PlaylistEntity
import com.kelsos.mbrc.features.playlists.domain.Playlist
import com.kelsos.mbrc.interfaces.data.Mapper

object PlaylistEntityMapper : Mapper<PlaylistEntity, Playlist> {
  override fun map(from: PlaylistEntity): Playlist = Playlist(
    name = from.name,
    url = from.url,
    id = from.id
  )
}