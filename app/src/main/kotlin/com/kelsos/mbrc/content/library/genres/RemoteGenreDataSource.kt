package com.kelsos.mbrc.content.library.genres

import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.interfaces.data.RemoteDataSource.Companion.LIMIT
import io.reactivex.Observable
import javax.inject.Inject

class RemoteGenreDataSource
@Inject constructor(private var service: LibraryService) : RemoteDataSource<Genre> {
  override fun fetch(): Observable<List<Genre>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getGenres(it!! * LIMIT, LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}
