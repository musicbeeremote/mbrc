package com.kelsos.mbrc.library.tracks

import com.kelsos.mbrc.repository.Repository
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Single

interface TrackRepository : Repository<Track> {
  fun getAlbumTracks(album: String, artist: String): Single<FlowCursorList<Track>>
  fun getNonAlbumTracks(artist: String): Single<FlowCursorList<Track>>
  fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>>
  fun getGenreTrackPaths(genre: String): Single<List<String>>
  fun getArtistTrackPaths(artist: String): Single<List<String>>
  fun getAllTrackPaths(): Single<List<String>>
}
