package com.kelsos.mbrc.feature.playback.nowplaying

import com.kelsos.mbrc.core.common.data.Mapper
import com.kelsos.mbrc.core.data.nowplaying.NowPlaying
import com.kelsos.mbrc.core.data.nowplaying.NowPlayingEntity
import com.kelsos.mbrc.core.networking.dto.NowPlayingDto

object NowPlayingDtoMapper : Mapper<NowPlayingDto, NowPlayingEntity> {
  override fun map(from: NowPlayingDto): NowPlayingEntity = NowPlayingEntity(
    from.title,
    from.artist,
    from.path,
    from.position
  )
}

fun NowPlayingDto.toEntity(): NowPlayingEntity = NowPlayingDtoMapper.map(this)

object NowPlayingEntityMapper : Mapper<NowPlayingEntity, NowPlaying> {
  override fun map(from: NowPlayingEntity): NowPlaying = NowPlaying(
    title = from.title,
    artist = from.artist,
    path = from.path,
    position = from.position,
    id = from.id
  )
}

fun NowPlayingEntity.toNowPlaying(): NowPlaying = NowPlayingEntityMapper.map(this)
