package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dao.GenreDao
import com.kelsos.mbrc.domain.Genre
import com.kelsos.mbrc.dto.library.GenreDto

object GenreMapper {

  fun map(data: List<GenreDto>): List<GenreDao> {
    return data.map { map(it) }.toList()
  }

  fun map(dto: GenreDto): GenreDao {
    val dao = GenreDao()
    dao.name = dto.name
    dao.id = dto.id
    dao.dateDeleted = dto.dateDeleted
    dao.dateUpdated = dto.dateUpdated
    dao.dateAdded = dto.dateAdded
    return dao
  }

  fun mapToModel(genres: List<GenreDao>): List<Genre> {
    return genres.map { mapToModel(it) }.toList()
  }

  fun mapToModel(dao: GenreDao): Genre {
    return Genre(dao.id, dao.name)
  }
}
