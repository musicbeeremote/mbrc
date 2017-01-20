package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Track
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single

interface TrackRepository : Repository<Track> {
  fun getAlbumTracks(album: String, artist: String): Single<FlowCursorList<Track>>
  fun getNonAlbumTracks(artist: String): Single<FlowCursorList<Track>>
  fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>>
  fun getGenreTrackPaths(genre: String): Single<List<String>>
  fun getArtistTrackPaths(artist: String): Single<List<String>>
  fun getAllTrackPaths(): Single<List<String>>
}
