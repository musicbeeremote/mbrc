package com.kelsos.mbrc.features.library.albums

import com.kelsos.mbrc.interfaces.data.Mapper

class AlbumDtoMapper : Mapper<AlbumDto, AlbumEntity> {
  override fun map(from: AlbumDto): AlbumEntity {
    return AlbumEntity(artist = from.artist, album = from.album)
  }
}