package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.interfaces.data.Mapper

object ArtistEntityMapper : Mapper<ArtistEntity, Artist> {
  override fun map(from: ArtistEntity): Artist {
    return Artist(
      from.artist,
      from.id
    )
  }
}

fun ArtistEntity.toArtist(): Artist {
  return ArtistEntityMapper.map(this)
}
