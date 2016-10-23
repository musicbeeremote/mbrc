package com.kelsos.mbrc.interactors

import android.graphics.Bitmap
import com.kelsos.mbrc.repository.TrackRepository
import rx.Observable
import javax.inject.Inject

class TrackCoverInteractorImpl
@Inject constructor(private val repository: TrackRepository) : TrackCoverInteractor {
  override fun load(reload: Boolean): Observable<Bitmap?> {
    return repository.getTrackCover(reload)
  }
}
