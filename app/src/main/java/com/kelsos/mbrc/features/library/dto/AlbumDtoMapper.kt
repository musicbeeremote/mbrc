package com.kelsos.mbrc.features.library.dto

import com.kelsos.mbrc.features.library.data.AlbumEntity
import com.kelsos.mbrc.interfaces.data.Mapper

class AlbumDtoMapper : Mapper<AlbumDto, AlbumEntity> {
  override fun map(from: AlbumDto): AlbumEntity {
    return AlbumEntity(
      artist = from.artist,
      album = from.album
    )
  }
}