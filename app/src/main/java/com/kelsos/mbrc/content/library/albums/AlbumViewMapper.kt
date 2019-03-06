package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.interfaces.data.Mapper

object AlbumViewMapper : Mapper<AlbumEntity, Album> {
  override fun map(from: AlbumEntity): Album {
    return Album(
      from.id,
      from.artist,
      from.album,
      from.cover
    )
  }
}

fun AlbumEntity.toAlbum(): Album {
  return AlbumViewMapper.map(this)
}
