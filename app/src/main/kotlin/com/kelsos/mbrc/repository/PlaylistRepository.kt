package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.dao.PlaylistDao
import com.kelsos.mbrc.data.dao.PlaylistTrackDao
import com.kelsos.mbrc.data.dao.PlaylistTrackInfoDao
import com.kelsos.mbrc.data.views.PlaylistTrackView
import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.dto.playlist.PlaylistTrackInfo
import rx.Observable

interface PlaylistRepository {
    fun getPlaylists(): Observable<List<Playlist>>

    fun getUserPlaylists(): Observable<List<Playlist>>

    fun savePlaylists(playlists: List<PlaylistDao>)

    fun getPlaylistTracks(playlistId: Long): Observable<List<PlaylistTrackView>>

    fun getTrackInfo(): Observable<List<PlaylistTrackInfo>>

    fun savePlaylistTrackInfo(data: List<PlaylistTrackInfoDao>)

    fun savePlaylistTracks(data: List<PlaylistTrackDao>)

    fun getPlaylistById(id: Long): PlaylistDao?

    fun getTrackInfoById(id: Long): PlaylistTrackInfoDao?
}
