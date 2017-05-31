package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.constants.Protocol
import com.kelsos.mbrc.interfaces.data.RemoteDataSource
import com.kelsos.mbrc.networking.ApiBase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteArtistDataSource
@Inject constructor(private val service: ApiBase) : RemoteDataSource<Artist> {
  override suspend fun fetch(): Flow<List<Artist>> {
    return service.getAllPages(Protocol.LibraryBrowseArtists, Artist::class)
  }
}
