package com.kelsos.mbrc.feature.library.artists

import com.kelsos.mbrc.core.common.data.Mapper
import com.kelsos.mbrc.core.data.library.artist.Artist
import com.kelsos.mbrc.core.data.library.artist.ArtistEntity
import com.kelsos.mbrc.core.networking.dto.ArtistDto

object ArtistDtoMapper : Mapper<ArtistDto, ArtistEntity> {
  override fun map(from: ArtistDto): ArtistEntity = ArtistEntity(from.artist)
}

object ArtistEntityMapper : Mapper<ArtistEntity, Artist> {
  override fun map(from: ArtistEntity): Artist = Artist(
    id = from.id,
    artist = from.artist
  )
}

fun ArtistDto.toEntity(): ArtistEntity = ArtistDtoMapper.map(this)

fun ArtistEntity.toArtist(): Artist = ArtistEntityMapper.map(this)
