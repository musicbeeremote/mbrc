package com.kelsos.mbrc.features.library.dto

import com.kelsos.mbrc.common.data.Mapper
import com.kelsos.mbrc.features.library.data.GenreEntity

object GenreDtoMapper : Mapper<GenreDto, GenreEntity> {
  override fun map(from: GenreDto): GenreEntity {
    return GenreEntity(from.genre)
  }
}

fun GenreDto.toEntity(): GenreEntity {
  return GenreDtoMapper.map(this)
}
