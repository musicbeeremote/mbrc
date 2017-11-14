package com.kelsos.mbrc.content.library.albums


import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.interfaces.data.RemoteDataSource.Companion.LIMIT
import io.reactivex.Observable
import javax.inject.Inject

class RemoteAlbumDataSource
@Inject constructor(private val service: LibraryService) : RemoteDataSource<Album> {
  override fun fetch(): Observable<List<Album>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getAlbums(it * LIMIT, LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}
