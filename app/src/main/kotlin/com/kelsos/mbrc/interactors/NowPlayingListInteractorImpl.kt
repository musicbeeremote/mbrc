package com.kelsos.mbrc.interactors

import com.kelsos.mbrc.domain.QueueTrack
import com.kelsos.mbrc.extensions.task
import com.kelsos.mbrc.repository.NowPlayingRepository
import rx.Observable
import javax.inject.Inject

class NowPlayingListInteractorImpl
@Inject constructor(private val repository: NowPlayingRepository) :
    NowPlayingListInteractor {

  override fun execute(): Observable<List<QueueTrack>> {
    return repository.getNowPlayingList().task()
  }
}
