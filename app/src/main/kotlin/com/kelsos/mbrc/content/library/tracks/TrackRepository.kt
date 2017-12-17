package com.kelsos.mbrc.content.library.tracks

import androidx.paging.DataSource
import com.kelsos.mbrc.interfaces.data.Repository

interface TrackRepository : Repository<Track> {
  suspend fun getAlbumTracks(album: String, artist: String): DataSource.Factory<Int, Track>
  suspend fun getNonAlbumTracks(artist: String): DataSource.Factory<Int, Track>
  suspend fun getAlbumTrackPaths(album: String, artist: String): List<String>
  suspend fun getGenreTrackPaths(genre: String): List<String>
  suspend fun getArtistTrackPaths(artist: String): List<String>
  suspend fun getAllTrackPaths(): List<String>
}
