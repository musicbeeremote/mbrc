package com.kelsos.mbrc.content.library.tracks

import com.kelsos.mbrc.interfaces.data.Mapper

class TrackDtoMapper : Mapper<TrackDto, TrackEntity> {
  override fun map(from: TrackDto): TrackEntity {
    return TrackEntity(
      from.artist,
      from.title,
      from.src,
      from.trackno,
      from.disc,
      from.albumArtist,
      from.album,
      from.genre,
      from.year
    )
  }
}