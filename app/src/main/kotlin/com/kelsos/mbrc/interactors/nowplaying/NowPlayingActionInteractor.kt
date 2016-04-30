package com.kelsos.mbrc.interactors.nowplaying

import rx.Observable

interface NowPlayingActionInteractor {
    fun play(path: String): Observable<Boolean>

    fun remove(position: Long): Observable<Boolean>

    fun move(from: Int, to: Int): Observable<Boolean>
}
