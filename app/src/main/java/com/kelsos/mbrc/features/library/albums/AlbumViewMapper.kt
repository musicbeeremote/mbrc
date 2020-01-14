package com.kelsos.mbrc.features.library.albums

import com.kelsos.mbrc.interfaces.data.Mapper

object AlbumViewMapper : Mapper<AlbumEntity, Album> {
  override fun map(from: AlbumEntity): Album {
    return Album(
      id = from.id,
      artist = from.artist,
      album = from.album,
      cover = from.cover
    )
  }
}

fun AlbumEntity.toAlbum(): Album {
  return AlbumViewMapper.map(this)
}
