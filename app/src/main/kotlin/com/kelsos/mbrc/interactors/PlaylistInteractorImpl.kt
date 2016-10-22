package com.kelsos.mbrc.interactors

import javax.inject.Inject
import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.repository.PlaylistRepository
import rx.Observable

class PlaylistInteractorImpl : PlaylistInteractor {

  @Inject private lateinit var repository: PlaylistRepository

  override fun getAllPlaylists(): Observable<List<Playlist>> = repository.getPlaylists().task()

  override fun getUserPlaylists(): Observable<List<Playlist>> = repository.getUserPlaylists().task()
}
