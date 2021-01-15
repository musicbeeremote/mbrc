package com.kelsos.mbrc.features.library.data

import com.kelsos.mbrc.common.data.Mapper

class TrackEntityMapper :
  Mapper<TrackEntity, Track> {
  override fun map(from: TrackEntity): Track {
    return Track(
      artist = from.artist,
      title = from.title,
      src = from.src,
      trackno = from.trackno,
      disc = from.disc,
      albumArtist = from.albumArtist,
      album = from.album,
      genre = from.genre,
      year = from.year,
      id = from.id
    )
  }
}
