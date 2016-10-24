package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.data.dao.PlaylistTrackInfoDao
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo
import rx.Observable

object PlaylistTrackInfoMapper {
  fun map(info: List<PlaylistTrackInfo>): List<PlaylistTrackInfoDao> {
    return info.map { map(it) }.toList()
  }

  fun map(item: PlaylistTrackInfo): PlaylistTrackInfoDao {
    val dao = PlaylistTrackInfoDao()
    dao.id = item.id
    dao.artist = item.artist
    dao.title = item.title
    dao.path = item.path
    dao.dateAdded = item.dateAdded
    dao.dateDeleted = item.dateDeleted
    dao.dateUpdated = item.dateUpdated
    return dao
  }
}
