package com.kelsos.mbrc.interactors

import javax.inject.Inject
import com.kelsos.mbrc.domain.TrackInfo
import com.kelsos.mbrc.repository.TrackRepository
import rx.Observable

class TrackInfoInteractorImpl : TrackInfoInteractor {
  @Inject private lateinit var repository: TrackRepository
  override fun load(reload: Boolean): Observable<TrackInfo> {
    return repository.getTrackInfo(reload)
  }
}
