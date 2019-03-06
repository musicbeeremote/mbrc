package com.kelsos.mbrc.content.library.genres

import com.kelsos.mbrc.interfaces.data.Mapper

object GenreEntityMapper : Mapper<GenreEntity, Genre> {
  override fun map(from: GenreEntity): Genre {
    return Genre(
      genre = from.genre,
      id = from.id
    )
  }
}

fun GenreEntity.toGenre(): Genre {
  return GenreEntityMapper.map(this)
}
