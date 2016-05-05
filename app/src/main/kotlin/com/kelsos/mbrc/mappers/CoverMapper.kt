package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dao.CoverDao
import com.kelsos.mbrc.dto.library.CoverDto

object CoverMapper {
  fun map(objects: List<CoverDto>): List<CoverDao> {
    return objects.map { map(it) }.toList()
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
