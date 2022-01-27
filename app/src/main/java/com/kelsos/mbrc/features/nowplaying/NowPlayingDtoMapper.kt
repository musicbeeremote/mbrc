package com.kelsos.mbrc.features.nowplaying

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingEntity

object NowPlayingDtoMapper :
  Mapper<NowPlayingDto, NowPlayingEntity> {
  override fun map(from: NowPlayingDto): NowPlayingEntity =
    NowPlayingEntity(
      from.title,
      from.artist,
      from.path,
      from.position,
    )
}

fun NowPlayingDto.toEntity(): NowPlayingEntity = NowPlayingDtoMapper.map(this)
