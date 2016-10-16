package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.repository.data.RemoteDataSource.Companion.LIMIT
import com.kelsos.mbrc.services.LibraryService
import rx.Observable
import javax.inject.Inject

class RemoteTrackDataSource
@Inject constructor(private val service: LibraryService) : RemoteDataSource<Track> {
  override fun fetch(): Observable<List<Track>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getTracks(it!! * LIMIT, LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }

}
