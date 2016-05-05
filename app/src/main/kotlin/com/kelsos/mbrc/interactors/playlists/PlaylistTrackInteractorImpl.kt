package com.kelsos.mbrc.interactors.playlists

import com.google.inject.Inject
import com.kelsos.mbrc.domain.PlaylistTrack
import com.kelsos.mbrc.mappers.PlaylistTrackMapper
import com.kelsos.mbrc.repository.PlaylistRepository
import rx.Observable
import rx.lang.kotlin.toObservable

class PlaylistTrackInteractorImpl : PlaylistTrackInteractor {
  @Inject private lateinit var repository: PlaylistRepository

  override fun execute(playlistId: Long): Observable<List<PlaylistTrack>> {
    return repository.getPlaylistTracks(playlistId)
        .flatMap { it.toObservable() }
        .map { PlaylistTrackMapper.map(it) }
        .toList()
  }
}
