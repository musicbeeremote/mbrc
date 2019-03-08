package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.Mapper

class ArtistEntityMapper : Mapper<ArtistEntity, Artist> {
  override fun map(from: ArtistEntity): Artist {
    return Artist(
      id = from.id,
      artist = from.artist
    )
  }
}