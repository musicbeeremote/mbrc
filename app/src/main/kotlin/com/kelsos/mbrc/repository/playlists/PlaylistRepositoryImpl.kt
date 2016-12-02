package com.kelsos.mbrc.repository.playlists

import com.kelsos.mbrc.domain.Playlist
import com.kelsos.mbrc.dto.BaseResponse
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class PlaylistRepositoryImpl
@Inject constructor() : PlaylistRepository {

  override fun play(path: String): Single<BaseResponse> {
    TODO()
  }

  override fun getAllCursor(): Single<FlowCursorList<Playlist>> {
    TODO()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Playlist>> {
    TODO()
  }

  override fun getRemote(): Completable {
    TODO()
  }

  override fun search(term: String): Single<FlowCursorList<Playlist>> {
    TODO()
  }
}
