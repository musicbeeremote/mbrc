package com.kelsos.mbrc.features.library.dto

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.library.data.ArtistEntity

object ArtistDtoMapper : Mapper<ArtistDto, ArtistEntity> {
  override fun map(from: ArtistDto): ArtistEntity {
    return ArtistEntity(from.artist)
  }
}

fun ArtistDto.toEntity(): ArtistEntity {
  return ArtistDtoMapper.map(this)
}
