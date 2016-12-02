package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.domain.Album
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class AlbumRepositoryImpl
@Inject constructor() : AlbumRepository {

  override fun getAlbumsByArtist(artist: String): Single<FlowCursorList<Album>> {
    TODO()
  }

  override fun getAllCursor(): Single<FlowCursorList<Album>> {
    TODO()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Album>> {
    TODO()
  }

  override fun getRemote(): Completable {
    TODO()
  }

  override fun search(term: String): Single<FlowCursorList<Album>> {
    TODO()
  }
}
