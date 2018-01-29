package com.kelsos.mbrc.content.library.tracks

import android.arch.paging.DataSource
import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface TrackRepository : Repository<TrackEntity> {
  fun getAlbumTracks(album: String, artist: String): Single<DataSource.Factory<Int, TrackEntity>>
  fun getNonAlbumTracks(artist: String): Single<DataSource.Factory<Int, TrackEntity>>
  fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>>
  fun getGenreTrackPaths(genre: String): Single<List<String>>
  fun getArtistTrackPaths(artist: String): Single<List<String>>
  fun getAllTrackPaths(): Single<List<String>>
}
