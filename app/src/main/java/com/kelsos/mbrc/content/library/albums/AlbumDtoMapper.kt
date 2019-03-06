package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.interfaces.data.Mapper

object AlbumDtoMapper : Mapper<AlbumDto, AlbumEntity> {
  override fun map(from: AlbumDto): AlbumEntity {
    return AlbumEntity(from.artist, from.album)
  }
}

fun AlbumDto.toEntity(): AlbumEntity {
  return AlbumDtoMapper.map(this)
}
