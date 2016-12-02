package com.kelsos.mbrc.repository.playlists

import com.kelsos.mbrc.data.dao.NowPlaying
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class NowPlayingRepositoryImpl
@Inject constructor() : NowPlayingRepository {
  override fun getAllCursor(): Single<FlowCursorList<NowPlaying>> {
    TODO()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<NowPlaying>> {
    TODO()
  }

  override fun getRemote(): Completable {
    TODO()
  }

  override fun search(term: String): Single<FlowCursorList<NowPlaying>> {
    TODO()
  }
}
