package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.domain.Genre
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class GenreRepositoryImpl
@Inject constructor() : GenreRepository {
  override fun getAllCursor(): Single<FlowCursorList<Genre>> {
    TODO()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Genre>> {
    TODO()
  }

  override fun getRemote(): Completable {
    TODO()
  }

  override fun search(term: String): Single<FlowCursorList<Genre>> {
    TODO()
  }
}
