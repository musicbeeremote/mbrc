package com.kelsos.mbrc.features.library.genres

import com.kelsos.mbrc.interfaces.data.Mapper

class GenreDtoMapper : Mapper<GenreDto, GenreEntity> {
  override fun map(from: GenreDto): GenreEntity {
    return GenreEntity(from.genre)
  }
}