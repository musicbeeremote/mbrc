package com.kelsos.mbrc.features.library.repositories

import androidx.paging.DataSource
import com.kelsos.mbrc.features.library.data.DataModel
import com.kelsos.mbrc.features.library.data.Track
import com.kelsos.mbrc.interfaces.data.Repository

interface TrackRepository : Repository<Track> {
  fun getAlbumTracks(album: String, artist: String): DataSource.Factory<Int, Track>
  fun getNonAlbumTracks(artist: String): DataSource.Factory<Int, Track>
  fun getAlbumTrackPaths(album: String, artist: String): List<String>
  fun getGenreTrackPaths(genre: String): List<String>
  fun getArtistTrackPaths(artist: String): List<String>
  fun getAllTrackPaths(): List<String>

  fun allTracks(): DataModel<Track>
}