package com.kelsos.mbrc.features.library.repositories

import androidx.paging.PagingData
import com.kelsos.mbrc.common.data.Repository
import com.kelsos.mbrc.features.library.data.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository : Repository<Track> {
  fun getAlbumTracks(album: String, artist: String): Flow<PagingData<Track>>
  fun getNonAlbumTracks(artist: String): Flow<PagingData<Track>>
  fun getAlbumTrackPaths(album: String, artist: String): List<String>
  fun getGenreTrackPaths(genre: String): List<String>
  fun getArtistTrackPaths(artist: String): List<String>
  fun getAllTrackPaths(): List<String>
}
