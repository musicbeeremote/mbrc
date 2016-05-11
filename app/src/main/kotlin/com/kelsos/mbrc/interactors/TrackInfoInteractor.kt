package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.TrackInfo
import rx.Observable

interface TrackInfoInteractor {
  fun load(reload: Boolean = false): Observable<TrackInfo>
}
