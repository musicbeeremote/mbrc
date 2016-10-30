package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.services.NowPlayingService
import rx.Observable
import javax.inject.Inject

class RemoteNowPlayingDataSource
@Inject constructor(private val service: NowPlayingService) : RemoteDataSource<NowPlaying> {
  override fun fetch(): Observable<List<NowPlaying>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getNowPlaying(it!! * RemoteDataSource.LIMIT, RemoteDataSource.LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}
