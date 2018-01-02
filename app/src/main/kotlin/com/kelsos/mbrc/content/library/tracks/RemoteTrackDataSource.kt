package com.kelsos.mbrc.content.library.tracks

import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.interfaces.data.RemoteDataSource.Companion.LIMIT
import com.kelsos.mbrc.ui.navigation.library.SyncProgress
import com.kelsos.mbrc.ui.navigation.library.SyncProgressProvider
import io.reactivex.Observable
import javax.inject.Inject

class RemoteTrackDataSource
@Inject
constructor(
    private val service: LibraryService,
    private val syncProgressProvider: SyncProgressProvider
) : RemoteDataSource<TrackDto> {
  override fun fetch(): Observable<List<TrackDto>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getTracks(it * LIMIT, LIMIT)
    }.doOnNext {
      syncProgressProvider.postValue(SyncProgress(it.offset, it.total, SyncProgress.TRACK))
    }.takeWhile { it.offset < it.total }.map { it.data }
  }

}
