package com.kelsos.mbrc.content.library.tracks

import androidx.paging.DataSource
import com.kelsos.mbrc.content.library.DataModel
import com.kelsos.mbrc.interfaces.data.Repository

interface TrackRepository : Repository<TrackEntity> {
  fun getAlbumTracks(album: String, artist: String): DataSource.Factory<Int, TrackEntity>
  fun getNonAlbumTracks(artist: String): DataSource.Factory<Int, TrackEntity>
  fun getAlbumTrackPaths(album: String, artist: String): List<String>
  fun getGenreTrackPaths(genre: String): List<String>
  fun getArtistTrackPaths(artist: String): List<String>
  fun getAllTrackPaths(): List<String>

  fun allTracks(): DataModel<TrackEntity>
}