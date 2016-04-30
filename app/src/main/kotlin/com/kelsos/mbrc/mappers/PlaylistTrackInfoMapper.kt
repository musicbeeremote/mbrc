package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dao.PlaylistTrackInfoDao
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo
import rx.Observable
import rx.functions.Func1

object PlaylistTrackInfoMapper {
    fun map(info: List<PlaylistTrackInfo>): List<PlaylistTrackInfoDao> {
        return Observable.from(info).map<PlaylistTrackInfoDao>(Func1<PlaylistTrackInfo, PlaylistTrackInfoDao> { map(it) }).toList().toBlocking().first()
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
