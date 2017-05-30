package com.kelsos.mbrc.library.artists

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.networking.ApiBase
import com.kelsos.mbrc.repository.data.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteArtistDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<Artist> {
  override suspend fun fetch(): Flow<List<Artist>> {
    return service.getAllPages(Protocol.LibraryBrowseArtists, Artist::class)
  }
}
