package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dao.CoverDao
import com.kelsos.mbrc.dto.library.CoverDto
import rx.Observable
import rx.functions.Func1

object CoverMapper {
    fun map(objects: List<CoverDto>): List<CoverDao> {
        return Observable.from(objects).map<CoverDao>(Func1<CoverDto, CoverDao> { map(it) }).toList().toBlocking().first()
    }

    fun map(item: CoverDto): CoverDao {
        val dao = CoverDao()
        dao.id = item.id
        dao.hash = item.hash
        dao.dateUpdated = item.dateUpdated
        dao.dateDeleted = item.dateDeleted
        dao.dateAdded = item.dateAdded
        return dao
    }
}
