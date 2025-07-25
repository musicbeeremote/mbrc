package com.kelsos.mbrc.features.nowplaying

import com.kelsos.mbrc.common.data.Mapper

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
