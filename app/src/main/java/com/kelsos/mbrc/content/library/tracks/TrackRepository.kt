package com.kelsos.mbrc.content.library.tracks

import androidx.paging.PagingData
import com.kelsos.mbrc.interfaces.data.Repository
import kotlinx.coroutines.flow.Flow

interface TrackRepository : Repository<Track> {
  suspend fun getAlbumTracks(album: String, artist: String): Flow<PagingData<Track>>
  suspend fun getNonAlbumTracks(artist: String): Flow<PagingData<Track>>
  suspend fun getAlbumTrackPaths(album: String, artist: String): List<String>
  suspend fun getGenreTrackPaths(genre: String): List<String>
  suspend fun getArtistTrackPaths(artist: String): List<String>
  suspend fun getAllTrackPaths(): List<String>
}
