package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.interfaces.data.RemoteDataSource.Companion.LIMIT
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
