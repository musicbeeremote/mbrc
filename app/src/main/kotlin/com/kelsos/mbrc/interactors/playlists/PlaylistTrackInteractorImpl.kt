package com.kelsos.mbrc.interactors.playlists

import com.kelsos.mbrc.domain.PlaylistTrack
import com.kelsos.mbrc.mappers.PlaylistTrackMapper
import com.kelsos.mbrc.repository.PlaylistRepository
import rx.Observable
import rx.lang.kotlin.toObservable
import javax.inject.Inject

class PlaylistTrackInteractorImpl
@Inject constructor(private val repository: PlaylistRepository) :
    PlaylistTrackInteractor {

  override fun execute(playlistId: Long): Observable<List<PlaylistTrack>> {
    return repository.getPlaylistTracks(playlistId)
        .flatMap { it.toObservable() }
        .map { PlaylistTrackMapper.map(it) }
        .toList()
  }
}
