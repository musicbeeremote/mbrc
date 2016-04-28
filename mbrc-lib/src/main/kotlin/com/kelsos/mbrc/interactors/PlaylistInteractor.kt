package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.Playlist
import rx.Observable

interface PlaylistInteractor {
    val allPlaylists: Observable<List<Playlist>>

    val userPlaylists: Observable<List<Playlist>>
}
