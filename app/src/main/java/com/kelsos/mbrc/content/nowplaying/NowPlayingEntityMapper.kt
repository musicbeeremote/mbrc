package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.interfaces.data.Mapper

object NowPlayingEntityMapper : Mapper<NowPlayingEntity, NowPlaying> {
  override fun map(from: NowPlayingEntity): NowPlaying {
    return NowPlaying(
      from.title,
      from.artist,
      from.path,
      from.position,
      from.id
    )
  }
}

fun NowPlayingEntity.toNowPlaying(): NowPlaying {
  return NowPlayingEntityMapper.map(this)
}
