package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.Playlist
import rx.Observable

interface PlaylistInteractor {
    fun getAllPlaylists(): Observable<List<Playlist>>

    fun getUserPlaylists(): Observable<List<Playlist>>
}
