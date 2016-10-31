package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.Playlist
import com.kelsos.mbrc.services.PlaylistService
import rx.Observable
import javax.inject.Inject


class RemotePlaylistDataSource
@Inject constructor(private val service: PlaylistService) : RemoteDataSource<Playlist> {
  override fun fetch(): Observable<List<Playlist>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getPlaylists(it!! * RemoteDataSource.LIMIT, RemoteDataSource.LIMIT)
    }.takeWhile { it.offset < it.total }.map { it.data }
  }

}
