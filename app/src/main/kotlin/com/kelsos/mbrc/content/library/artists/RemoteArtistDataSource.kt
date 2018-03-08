package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.interfaces.data.RemoteDataSource.Companion.LIMIT
import com.kelsos.mbrc.ui.navigation.library.SyncProgress
import com.kelsos.mbrc.ui.navigation.library.SyncProgressProvider
import io.reactivex.Observable
import javax.inject.Inject

class RemoteArtistDataSource
@Inject
constructor(
  private val service: LibraryService,
  private val syncProgressProvider: SyncProgressProvider
) : RemoteDataSource<ArtistDto> {
  override fun fetch(): Observable<List<ArtistDto>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getArtists(it * LIMIT, LIMIT)
    }.doOnNext {
      syncProgressProvider.postValue(SyncProgress(it.offset, it.total, SyncProgress.ARTIST))
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}