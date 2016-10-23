package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.repository.TrackRepository
import rx.Observable
import javax.inject.Inject

class TrackInfoInteractorImpl
@Inject constructor(private val repository: TrackRepository) :
    TrackInfoInteractor {

  override fun load(reload: Boolean): Observable<TrackInfo> {
    return repository.getTrackInfo(reload)
  }
}
