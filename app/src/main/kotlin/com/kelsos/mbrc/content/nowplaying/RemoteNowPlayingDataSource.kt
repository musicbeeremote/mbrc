package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import io.reactivex.Observable
import javax.inject.Inject

class RemoteNowPlayingDataSource
@Inject
constructor(private val service: NowPlayingService) : RemoteDataSource<NowPlayingDto> {
  override fun fetch(): Observable<List<NowPlayingDto>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getNowPlaying(it * RemoteDataSource.LIMIT, RemoteDataSource.LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}