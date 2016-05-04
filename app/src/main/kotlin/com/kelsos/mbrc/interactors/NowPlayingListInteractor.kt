package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.QueueTrack

import rx.Observable

interface NowPlayingListInteractor {
  fun execute(): Observable<List<QueueTrack>>
}
