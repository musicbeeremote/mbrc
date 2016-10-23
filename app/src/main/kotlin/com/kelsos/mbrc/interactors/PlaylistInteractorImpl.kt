package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.repository.PlaylistRepository
import rx.Observable
import javax.inject.Inject

class PlaylistInteractorImpl
@Inject constructor(private val repository: PlaylistRepository) : PlaylistInteractor {

  override fun getAllPlaylists(): Observable<List<Playlist>> = repository.getPlaylists().task()

  override fun getUserPlaylists(): Observable<List<Playlist>> = repository.getUserPlaylists().task()
}
