package com.kelsos.mbrc.content.library.genres

import com.kelsos.mbrc.content.library.LibraryService
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.interfaces.data.RemoteDataSource.Companion.LIMIT
import com.kelsos.mbrc.ui.navigation.library.SyncProgress
import com.kelsos.mbrc.ui.navigation.library.SyncProgressProvider
import io.reactivex.Observable
import javax.inject.Inject

class RemoteGenreDataSource
@Inject
constructor(
  private var service: LibraryService,
  private val syncProgressProvider: SyncProgressProvider
) : RemoteDataSource<GenreDto> {
  override fun fetch(): Observable<List<GenreDto>> {
    return Observable.range(0, Integer.MAX_VALUE).flatMap {
      service.getGenres(it * LIMIT, LIMIT)
    }.doOnNext {
      syncProgressProvider.postValue(SyncProgress(it.offset, it.total, SyncProgress.GENRE))
    }.takeWhile { it.offset < it.total }.map { it.data }
  }
}