package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.repository.TrackRepository

import rx.Observable

class TrackLyricsInteractorImpl : TrackLyricsInteractor {

    @Inject private lateinit var repository: TrackRepository

    override fun execute(reload: Boolean): Observable<List<String>> {
        return repository.getTrackLyrics(reload)
    }
}
