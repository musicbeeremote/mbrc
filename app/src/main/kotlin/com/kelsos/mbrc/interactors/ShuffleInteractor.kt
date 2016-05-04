package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.annotations.Shuffle.State
import rx.Observable

interface ShuffleInteractor {
  fun getShuffle(): Observable<String>
  fun updateShuffle(@State state: String): Observable<String>
}
