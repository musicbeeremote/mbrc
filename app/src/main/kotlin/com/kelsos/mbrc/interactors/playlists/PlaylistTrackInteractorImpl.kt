package com.kelsos.mbrc.interactors.playlists

import com.google.inject.Inject
import com.kelsos.mbrc.dao.views.PlaylistTrackView
import com.kelsos.mbrc.domain.PlaylistTrack
import com.kelsos.mbrc.mappers.PlaylistTrackMapper
import com.kelsos.mbrc.repository.PlaylistRepository
import rx.Observable
import rx.functions.Func1

class PlaylistTrackInteractorImpl : PlaylistTrackInteractor {
    @Inject private lateinit var repository: PlaylistRepository

    override fun execute(playlistId: Long): Observable<List<PlaylistTrack>> {
        return repository.getPlaylistTracks(playlistId)
                .flatMap<PlaylistTrackView>(Func1<List<PlaylistTrackView>, Observable<out PlaylistTrackView>> {
                    Observable.from(it)
                }).map<PlaylistTrack>(Func1<PlaylistTrackView, PlaylistTrack> {
            PlaylistTrackMapper.map(it)
        }).toList()
    }
}
