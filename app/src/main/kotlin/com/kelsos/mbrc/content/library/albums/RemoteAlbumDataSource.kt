package com.kelsos.mbrc.content.library.albums

import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.interfaces.data.RemoteDataSource.Companion.LIMIT
import com.kelsos.mbrc.ui.navigation.library.SyncProgress
import com.kelsos.mbrc.ui.navigation.library.SyncProgress.Companion.ALBUM
import com.kelsos.mbrc.ui.navigation.library.SyncProgressProvider
import io.reactivex.Observable
import javax.inject.Inject

class RemoteAlbumDataSource
@Inject
constructor(
    private val service: LibraryService,
    private val syncProgressProvider: SyncProgressProvider
) : RemoteDataSource<AlbumDto> {
  override fun fetch(): Observable<List<AlbumDto>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getAlbums(it * LIMIT, LIMIT)
    }.doOnNext {
      syncProgressProvider.postValue(SyncProgress(it.offset, it.total, ALBUM))
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}
