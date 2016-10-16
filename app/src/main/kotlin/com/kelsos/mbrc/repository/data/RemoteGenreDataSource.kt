package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.library.Genre
import com.kelsos.mbrc.repository.data.RemoteDataSource.Companion.LIMIT
import com.kelsos.mbrc.services.LibraryService
import rx.Observable
import javax.inject.Inject

class RemoteGenreDataSource
@Inject constructor(private var service: LibraryService) : RemoteDataSource<Genre> {
  override fun fetch(): Observable<List<Genre>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getGenres(it!! * LIMIT, LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}
