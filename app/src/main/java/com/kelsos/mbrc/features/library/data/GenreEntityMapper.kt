package com.kelsos.mbrc.features.library.data

import com.kelsos.mbrc.common.data.Mapper

object GenreEntityMapper : Mapper<GenreEntity, Genre> {
  override fun map(from: GenreEntity): Genre =
    Genre(
      genre = from.genre,
      id = from.id,
    )
}

fun GenreEntity.toGenre(): Genre = GenreEntityMapper.map(this)
