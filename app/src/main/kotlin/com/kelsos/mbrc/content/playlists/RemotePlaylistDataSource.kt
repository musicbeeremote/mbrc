package com.kelsos.mbrc.content.playlists

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import io.reactivex.Observable
import javax.inject.Inject

class RemotePlaylistDataSource
@Inject
constructor(private val service: PlaylistService) : RemoteDataSource<PlaylistDto> {
  override fun fetch(): Observable<List<PlaylistDto>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.fetch(it * RemoteDataSource.LIMIT, RemoteDataSource.LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}