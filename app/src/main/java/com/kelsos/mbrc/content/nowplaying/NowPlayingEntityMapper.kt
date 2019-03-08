package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.interfaces.data.Mapper

object NowPlayingEntityMapper : Mapper<NowPlayingEntity, NowPlaying> {
  override fun map(from: NowPlayingEntity): NowPlaying {
    return NowPlaying(
      title = from.title,
      artist = from.artist,
      path = from.path,
      position = from.position,
      id = from.id
    )
  }
}

fun NowPlayingEntity.toNowPlaying(): NowPlaying {
  return NowPlayingEntityMapper.map(this)
}
