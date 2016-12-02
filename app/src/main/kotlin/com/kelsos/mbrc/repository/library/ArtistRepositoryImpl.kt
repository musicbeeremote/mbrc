package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.domain.Artist
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class ArtistRepositoryImpl
@Inject constructor() : ArtistRepository {


  override fun getArtistByGenre(genre: String): Single<FlowCursorList<Artist>> {
    TODO()
  }

  override fun getAllCursor(): Single<FlowCursorList<Artist>> {
    TODO()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Artist>> {
    TODO()
  }

  override fun getRemote(): Completable {
    TODO()
  }

  override fun search(term: String): Single<FlowCursorList<Artist>> {
    TODO()
  }
}
