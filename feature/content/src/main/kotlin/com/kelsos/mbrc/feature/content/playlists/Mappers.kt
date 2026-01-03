package com.kelsos.mbrc.feature.content.playlists

import com.kelsos.mbrc.core.common.data.Mapper
import com.kelsos.mbrc.core.data.playlist.Playlist
import com.kelsos.mbrc.core.data.playlist.PlaylistEntity
import com.kelsos.mbrc.core.networking.dto.PlaylistDto

object PlaylistEntityMapper : Mapper<PlaylistEntity, Playlist> {
  override fun map(from: PlaylistEntity): Playlist = Playlist(
    name = from.name,
    url = from.url,
    id = from.id
  )
}

object PlaylistDtoMapper : Mapper<PlaylistDto, PlaylistEntity> {
  override fun map(from: PlaylistDto): PlaylistEntity = PlaylistEntity(
    name = from.name,
    url = from.url
  )
}

fun PlaylistEntity.toPlaylist(): Playlist = PlaylistEntityMapper.map(this)

fun PlaylistDto.toEntity(): PlaylistEntity = PlaylistDtoMapper.map(this)
