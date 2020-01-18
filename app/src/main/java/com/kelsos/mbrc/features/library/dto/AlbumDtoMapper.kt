package com.kelsos.mbrc.features.library.dto

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.library.data.AlbumEntity

class AlbumDtoMapper :
  Mapper<AlbumDto, AlbumEntity> {
  override fun map(from: AlbumDto): AlbumEntity {
    return AlbumEntity(
      artist = from.artist,
      album = from.album
    )
  }
}