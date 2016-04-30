package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.repository.PlaylistRepository
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PlaylistInteractorImpl : PlaylistInteractor {

    @Inject private lateinit var repository: PlaylistRepository

    override val allPlaylists: Observable<List<Playlist>>
        get() = repository.getPlaylists().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    override val userPlaylists: Observable<List<Playlist>>
        get() = repository.getUserPlaylists().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}
