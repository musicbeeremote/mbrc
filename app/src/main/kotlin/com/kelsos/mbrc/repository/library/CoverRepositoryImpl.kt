package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.data.dao.Cover
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class CoverRepositoryImpl
@Inject constructor() : CoverRepository {
  override fun getAllCursor(): Single<FlowCursorList<Cover>> {
    TODO()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Cover>> {
    TODO()
  }

  override fun getRemote(): Completable {
    TODO()
  }

  override fun search(term: String): Single<FlowCursorList<Cover>> {
    TODO()
  }

}
