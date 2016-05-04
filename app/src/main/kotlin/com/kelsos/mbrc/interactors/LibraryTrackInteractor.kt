package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.Track
import rx.Observable

interface LibraryTrackInteractor {
  fun execute(page: Int, items: Int): Observable<List<Track>>
}
