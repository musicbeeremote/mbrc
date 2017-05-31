package com.kelsos.mbrc.content.library.tracks

import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.interfaces.data.RemoteDataSource.Companion.LIMIT
import io.reactivex.Observable
import javax.inject.Inject

class RemoteTrackDataSource
@Inject constructor(private val service: LibraryService) : RemoteDataSource<Track> {
  override fun fetch(): Observable<List<Track>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getTracks(it!! * LIMIT, LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }

}
