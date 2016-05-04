package com.kelsos.mbrc.interactors

import com.google.inject.Inject
import com.kelsos.mbrc.domain.QueueTrack
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.repository.NowPlayingRepository
import rx.Observable

class NowPlayingListInteractorImpl : NowPlayingListInteractor {
  @Inject private lateinit var repository: NowPlayingRepository

  override fun execute(): Observable<List<QueueTrack>> {
    return repository.getNowPlayingList().task()
  }
}
