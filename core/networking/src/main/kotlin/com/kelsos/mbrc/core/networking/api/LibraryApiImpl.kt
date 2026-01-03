package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.common.data.Progress
import com.kelsos.mbrc.core.networking.ApiBase
import com.kelsos.mbrc.core.networking.client.ResponseWithPayload
import com.kelsos.mbrc.core.networking.dto.AlbumCoverDto
import com.kelsos.mbrc.core.networking.dto.AlbumDto
import com.kelsos.mbrc.core.networking.dto.ArtistDto
import com.kelsos.mbrc.core.networking.dto.CoverDto
import com.kelsos.mbrc.core.networking.dto.GenreDto
import com.kelsos.mbrc.core.networking.dto.TrackDto
import com.kelsos.mbrc.core.networking.protocol.base.Protocol
import kotlinx.coroutines.flow.Flow

class LibraryApiImpl(private val apiBase: ApiBase) : LibraryApi {
  override fun getAlbums(progress: Progress?): Flow<List<AlbumDto>> =
    apiBase.getAllPages(Protocol.LibraryBrowseAlbums, AlbumDto::class, progress)

  override fun getArtists(progress: Progress?): Flow<List<ArtistDto>> =
    apiBase.getAllPages(Protocol.LibraryBrowseArtists, ArtistDto::class, progress)

  override fun getGenres(progress: Progress?): Flow<List<GenreDto>> =
    apiBase.getAllPages(Protocol.LibraryBrowseGenres, GenreDto::class, progress)

  override fun getTracks(progress: Progress?): Flow<List<TrackDto>> =
    apiBase.getAllPages(Protocol.LibraryBrowseTracks, TrackDto::class, progress)

  override fun getCovers(
    covers: List<AlbumCoverDto>,
    progress: Progress?
  ): Flow<ResponseWithPayload<AlbumCoverDto, CoverDto>> =
    apiBase.getAll(Protocol.LibraryCover, covers, CoverDto::class, progress)
}
