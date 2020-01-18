package com.kelsos.mbrc.features.library.dto

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.library.data.AlbumEntity

object AlbumDtoMapper : Mapper<AlbumDto, AlbumEntity> {
  override fun map(from: AlbumDto): AlbumEntity {
    return AlbumEntity(
      artist = from.artist,
      album = from.album
    )
  }
}

fun AlbumDto.toEntity(): AlbumEntity {
  return AlbumDtoMapper.map(this)
}
