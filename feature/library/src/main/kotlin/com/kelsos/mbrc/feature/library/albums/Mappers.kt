package com.kelsos.mbrc.feature.library.albums

import com.kelsos.mbrc.core.common.data.Mapper
import com.kelsos.mbrc.core.data.library.album.Album
import com.kelsos.mbrc.core.data.library.album.AlbumEntity
import com.kelsos.mbrc.core.networking.dto.AlbumDto

object AlbumDtoMapper : Mapper<AlbumDto, AlbumEntity> {
  override fun map(from: AlbumDto): AlbumEntity = AlbumEntity(
    artist = from.artist,
    album = from.album
  )
}

object AlbumEntityMapper : Mapper<AlbumEntity, Album> {
  override fun map(from: AlbumEntity): Album = Album(
    id = from.id,
    artist = from.artist,
    album = from.album,
    cover = from.cover
  )
}

fun AlbumEntity.toAlbum(): Album = AlbumEntityMapper.map(this)

fun AlbumDto.toEntity(): AlbumEntity = AlbumDtoMapper.map(this)
