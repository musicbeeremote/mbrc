package com.kelsos.mbrc.features.playlists

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.playlists.data.PlaylistEntity

object PlaylistDtoMapper :
  Mapper<PlaylistDto, PlaylistEntity> {
  override fun map(from: PlaylistDto): PlaylistEntity = PlaylistEntity(
    name = from.name,
    url = from.url
  )
}

fun PlaylistDto.toEntity(): PlaylistEntity {
  return PlaylistDtoMapper.map(this)
}
