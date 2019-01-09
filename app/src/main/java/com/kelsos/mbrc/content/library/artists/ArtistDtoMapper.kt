package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.Mapper

class ArtistDtoMapper : Mapper<ArtistDto, ArtistEntity> {
  override fun map(from: ArtistDto): ArtistEntity {
    return ArtistEntity(from.artist)
  }
}
