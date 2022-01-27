package com.kelsos.mbrc.features.library.dto

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.library.data.AlbumEntity

object AlbumDtoMapper : Mapper<AlbumDto, AlbumEntity> {
  override fun map(from: AlbumDto): AlbumEntity =
    AlbumEntity(
      artist = from.artist,
      album = from.album,
    )
}

fun AlbumDto.toEntity(): AlbumEntity = AlbumDtoMapper.map(this)
