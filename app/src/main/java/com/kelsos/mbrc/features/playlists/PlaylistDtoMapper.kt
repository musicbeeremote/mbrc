package com.kelsos.mbrc.features.playlists

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.playlists.data.PlaylistEntity

object PlaylistDtoMapper :
  Mapper<PlaylistDto, PlaylistEntity> {
  override fun map(from: PlaylistDto): PlaylistEntity = PlaylistEntity(from.name, from.url)
}
