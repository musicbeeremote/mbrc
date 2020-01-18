package com.kelsos.mbrc.features.library.data

import com.kelsos.mbrc.common.data.Mapper

object AlbumEntityMapper : Mapper<AlbumEntity, Album> {
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
  return AlbumEntityMapper.map(this)
}
