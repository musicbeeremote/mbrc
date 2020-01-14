package com.kelsos.mbrc.features.library.albums

import com.kelsos.mbrc.interfaces.data.Mapper

class AlbumViewMapper : Mapper<AlbumEntity, Album> {
  override fun map(from: AlbumEntity): Album {
    return Album(
    id = from.id,
    artist = from.artist,
    album = from.album,
    albumArtist = "",
    genre = "",
    sortableYear = "",
    dateAdded = from.dateAdded
    )
  }
}