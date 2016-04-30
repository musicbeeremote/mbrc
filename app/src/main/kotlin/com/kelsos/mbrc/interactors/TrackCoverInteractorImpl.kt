package com.kelsos.mbrc.interactors

import android.graphics.Bitmap

import com.google.inject.Inject
import com.kelsos.mbrc.repository.TrackRepository

import rx.Observable

class TrackCoverInteractorImpl : TrackCoverInteractor {
    @Inject private lateinit var repository: TrackRepository
    override fun execute(b: Boolean): Observable<Bitmap?> {
        return repository.getTrackCover(b)
    }
}
