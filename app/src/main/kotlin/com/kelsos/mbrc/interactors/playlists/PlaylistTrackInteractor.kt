package com.kelsos.mbrc.interactors.playlists

import com.kelsos.mbrc.domain.PlaylistTrack
import rx.Observable

interface PlaylistTrackInteractor {
    fun execute(playlistId: Long): Observable<List<PlaylistTrack>>
}
