package com.kelsos.mbrc.features.library.artists

import com.kelsos.mbrc.interfaces.data.Mapper

object ArtistDtoMapper : Mapper<ArtistDto, ArtistEntity> {
  override fun map(from: ArtistDto): ArtistEntity {
    return ArtistEntity(from.artist)
  }
}

fun ArtistDto.toEntity(): ArtistEntity {
  return ArtistDtoMapper.map(this)
}
