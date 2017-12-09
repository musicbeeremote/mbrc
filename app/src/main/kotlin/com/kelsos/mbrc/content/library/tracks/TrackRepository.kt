package com.kelsos.mbrc.content.library.tracks

import com.kelsos.mbrc.interfaces.data.Repository
import io.reactivex.Single

interface TrackRepository : Repository<Track> {
  fun getAlbumTracks(album: String, artist: String): Single<List<Track>>
  fun getNonAlbumTracks(artist: String): Single<List<Track>>
  fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>>
  fun getGenreTrackPaths(genre: String): Single<List<String>>
  fun getArtistTrackPaths(artist: String): Single<List<String>>
  fun getAllTrackPaths(): Single<List<String>>
}
