package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.interfaces.data.Mapper

class NowPlayingEntityMapper : Mapper<NowPlayingEntity, NowPlaying> {
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