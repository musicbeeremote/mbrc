package com.kelsos.mbrc.repository

import com.kelsos.mbrc.domain.QueueTrack

import rx.Observable

interface NowPlayingRepository {
    fun getNowPlayingList(): Observable<List<QueueTrack>>
}
