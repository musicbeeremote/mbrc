package com.kelsos.mbrc.library.genres

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.repository.data.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteGenreDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<Genre> {
  override suspend fun fetch(): Flow<List<Genre>> {
    return service.getAllPages(Protocol.LibraryBrowseGenres, Genre::class)
  }
}
