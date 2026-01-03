package com.kelsos.mbrc.feature.library.genres

import com.kelsos.mbrc.core.common.data.Mapper
import com.kelsos.mbrc.core.data.library.genre.Genre
import com.kelsos.mbrc.core.data.library.genre.GenreEntity
import com.kelsos.mbrc.core.networking.dto.GenreDto

object GenreDtoMapper : Mapper<GenreDto, GenreEntity> {
  override fun map(from: GenreDto): GenreEntity = GenreEntity(from.genre)
}

object GenreEntityMapper : Mapper<GenreEntity, Genre> {
  override fun map(from: GenreEntity): Genre = Genre(
    genre = from.genre,
    id = from.id
  )
}

fun GenreDto.toEntity(): GenreEntity = GenreDtoMapper.map(this)

fun GenreEntity.toGenre(): Genre = GenreEntityMapper.map(this)
