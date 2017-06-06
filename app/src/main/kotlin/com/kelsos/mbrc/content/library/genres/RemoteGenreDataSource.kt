package com.kelsos.mbrc.content.library.genres

import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.networking.protocol.Protocol
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteGenreDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<Genre> {
  override suspend fun fetch(): Flow<List<Genre>> {
    return service.getAllPages(Protocol.LibraryBrowseGenres, Genre::class)
  }
}
