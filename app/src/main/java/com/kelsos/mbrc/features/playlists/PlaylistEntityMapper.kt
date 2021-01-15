package com.kelsos.mbrc.features.playlists

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.playlists.data.PlaylistEntity
import com.kelsos.mbrc.features.playlists.domain.Playlist

object PlaylistEntityMapper :
  Mapper<PlaylistEntity, Playlist> {
  override fun map(from: PlaylistEntity): Playlist = Playlist(
    name = from.name,
    url = from.url,
    id = from.id
  )
}
