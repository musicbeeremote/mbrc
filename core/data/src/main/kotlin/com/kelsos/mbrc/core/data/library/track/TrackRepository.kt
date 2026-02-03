package com.kelsos.mbrc.core.data.library.track

import androidx.paging.PagingData
import com.kelsos.mbrc.core.common.settings.SortOrder
import com.kelsos.mbrc.core.common.settings.TrackSortField
import com.kelsos.mbrc.core.data.Repository
import kotlinx.coroutines.flow.Flow

sealed class TrackQuery {
  data class Album(val album: String, val artist: String) : TrackQuery()

  data class Genre(val genre: String) : TrackQuery()

  data class Artist(val artist: String) : TrackQuery()

  object All : TrackQuery()
}

sealed class PagingTrackQuery {
  data class Album(val album: String, val artist: String) : PagingTrackQuery()

  data class NonAlbum(val artist: String) : PagingTrackQuery()
}

interface TrackRepository : Repository<Track> {
  fun getTracks(query: PagingTrackQuery): Flow<PagingData<Track>>

  fun getTrackPaths(query: TrackQuery): List<String>

  suspend fun getByPath(path: String): Track?

  fun getAll(field: TrackSortField, order: SortOrder): Flow<PagingData<Track>>

  fun search(term: String, field: TrackSortField, order: SortOrder): Flow<PagingData<Track>>
}
