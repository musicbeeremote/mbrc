package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dao.GenreDao
import com.kelsos.mbrc.domain.Genre
import com.kelsos.mbrc.dto.library.GenreDto
import rx.Observable
import rx.functions.Func1

object GenreMapper {

    fun map(data: List<GenreDto>): List<GenreDao> {
        return Observable.from(data).map<GenreDao>(Func1<GenreDto, GenreDao> { map(it) }).toList().toBlocking().first()
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
        return Observable.from(genres).map<Genre>(Func1<GenreDao, Genre> { mapToModel(it) }).toList().toBlocking().first()
    }

    fun mapToModel(dao: GenreDao): Genre {
        return Genre(dao.id, dao.name)
    }
}
