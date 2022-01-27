package com.kelsos.mbrc.features.library.data

import com.kelsos.mbrc.common.data.Mapper

object ArtistEntityMapper : Mapper<ArtistEntity, Artist> {
  override fun map(from: ArtistEntity): Artist =
    Artist(
      id = from.id,
      artist = from.artist,
    )
}

fun ArtistEntity.toArtist(): Artist = ArtistEntityMapper.map(this)
