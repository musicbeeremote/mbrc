package com.kelsos.mbrc.interactors

import rx.Observable

interface ShuffleInteractor {
    fun getShuffle(): Observable<String>
    fun updateShuffle(@com.kelsos.mbrc.annotations.Shuffle.State state: String): Observable<String>
}
