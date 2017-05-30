package com.kelsos.mbrc.library.artists

import com.kelsos.mbrc.library.LibraryService
import com.kelsos.mbrc.repository.data.RemoteDataSource
import com.kelsos.mbrc.repository.data.RemoteDataSource.Companion.LIMIT
import io.reactivex.Observable
import javax.inject.Inject

class RemoteArtistDataSource
@Inject constructor(private val service: LibraryService) : RemoteDataSource<Artist> {
  override fun fetch(): Observable<List<Artist>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getArtists(it!! * LIMIT, LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}
