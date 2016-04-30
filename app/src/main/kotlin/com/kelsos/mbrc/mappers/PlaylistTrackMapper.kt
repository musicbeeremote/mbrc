package com.kelsos.mbrc.mappers

import com.kelsos.mbrc.dao.PlaylistDao
import com.kelsos.mbrc.dao.PlaylistTrackDao
import com.kelsos.mbrc.dao.PlaylistTrackInfoDao
import com.kelsos.mbrc.dao.views.PlaylistTrackView
import com.kelsos.mbrc.dto.playlist.PlaylistTrack

object PlaylistTrackMapper {
    fun map(data: List<PlaylistTrack>,
            playlistItemProvider: (Long) -> PlaylistDao?,
            infoDaoItemProvider: (Long) -> PlaylistTrackInfoDao?):List<PlaylistTrackDao> {
        return data.map {
            map(it, playlistItemProvider, infoDaoItemProvider)
        }.toList()
    }

    fun map(data: PlaylistTrack,
            playlistItemProvider: (Long) -> PlaylistDao?,
            infoDaoItemProvider: (Long) -> PlaylistTrackInfoDao?): PlaylistTrackDao {

        val dao = PlaylistTrackDao()
        dao.id = data.id
        dao.playlist = playlistItemProvider.invoke(data.playlistId)
        dao.trackInfo = infoDaoItemProvider.invoke(data.trackInfoId)
        dao.position = data.position
        dao.dateAdded = data.dateAdded
        dao.dateUpdated = data.dateUpdated
        dao.dateDeleted = data.dateDeleted

        return dao
    }

    fun map(view: PlaylistTrackView): com.kelsos.mbrc.domain.PlaylistTrack {
        val track = com.kelsos.mbrc.domain.PlaylistTrack()
        track.path = view.path
        track.position = view.position
        track.id = view.id
        track.artist = view.artist
        track.title = view.title
        return track
    }
}
