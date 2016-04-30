package com.kelsos.mbrc.interactors

import rx.Observable

interface TrackLyricsInteractor {
    fun execute(reload: Boolean): Observable<List<String>>
}
