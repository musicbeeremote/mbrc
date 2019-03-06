package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.interfaces.data.Mapper

object NowPlayingDtoMapper : Mapper<NowPlayingDto, NowPlayingEntity> {
  override fun map(from: NowPlayingDto): NowPlayingEntity {
    return NowPlayingEntity(from.title, from.artist, from.path, from.position)
  }
}

fun NowPlayingDto.toEntity(): NowPlayingEntity {
  return NowPlayingDtoMapper.map(this)
}
