package com.kelsos.mbrc.features.nowplaying

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.nowplaying.data.NowPlayingEntity
import com.kelsos.mbrc.features.nowplaying.domain.NowPlaying

object NowPlayingEntityMapper :
  Mapper<NowPlayingEntity, NowPlaying> {
  override fun map(from: NowPlayingEntity): NowPlaying =
    NowPlaying(
      title = from.title,
      artist = from.artist,
      path = from.path,
      position = from.position,
      id = from.id,
    )
}

fun NowPlayingEntity.toNowPlaying(): NowPlaying = NowPlayingEntityMapper.map(this)
