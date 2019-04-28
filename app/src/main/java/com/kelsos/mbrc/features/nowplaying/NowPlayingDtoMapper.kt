package com.kelsos.mbrc.features.nowplaying

import com.kelsos.mbrc.features.nowplaying.data.NowPlayingEntity
import com.kelsos.mbrc.interfaces.data.Mapper

object NowPlayingDtoMapper : Mapper<NowPlayingDto, NowPlayingEntity> {
  override fun map(from: NowPlayingDto): NowPlayingEntity =
    NowPlayingEntity(
      from.title,
      from.artist,
      from.path,
      from.position
    )
}

fun NowPlayingDto.toEntity(): NowPlayingEntity {
  return NowPlayingDtoMapper.map(this)
}
