package com.kelsos.mbrc.core.networking.api

import com.kelsos.mbrc.core.common.data.Progress
import com.kelsos.mbrc.core.networking.client.ResponseWithPayload
import com.kelsos.mbrc.core.networking.dto.AlbumCoverDto
import com.kelsos.mbrc.core.networking.dto.AlbumDto
import com.kelsos.mbrc.core.networking.dto.ArtistDto
import com.kelsos.mbrc.core.networking.dto.CoverDto
import com.kelsos.mbrc.core.networking.dto.GenreDto
import com.kelsos.mbrc.core.networking.dto.TrackDto
import kotlinx.coroutines.flow.Flow

interface LibraryApi {
  fun getAlbums(progress: Progress?): Flow<List<AlbumDto>>

  fun getArtists(progress: Progress?): Flow<List<ArtistDto>>

  fun getGenres(progress: Progress?): Flow<List<GenreDto>>

  fun getTracks(progress: Progress?): Flow<List<TrackDto>>

  fun getCovers(
    covers: List<AlbumCoverDto>,
    progress: Progress?
  ): Flow<ResponseWithPayload<AlbumCoverDto, CoverDto>>
}
