package com.kelsos.mbrc.features.library.dto

import com.kelsos.mbrc.features.library.data.ArtistEntity
import com.kelsos.mbrc.interfaces.data.Mapper

class ArtistDtoMapper : Mapper<ArtistDto, ArtistEntity> {
  override fun map(from: ArtistDto): ArtistEntity {
    return ArtistEntity(from.artist)
  }
}