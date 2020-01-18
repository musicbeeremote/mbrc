package com.kelsos.mbrc.features.library.dto

import com.kelsos.mbrc.features.library.data.GenreEntity
import com.kelsos.mbrc.features.library.dto.GenreDto
import com.kelsos.mbrc.interfaces.data.Mapper

class GenreDtoMapper : Mapper<GenreDto, GenreEntity> {
  override fun map(from: GenreDto): GenreEntity {
    return GenreEntity(from.genre)
  }
}