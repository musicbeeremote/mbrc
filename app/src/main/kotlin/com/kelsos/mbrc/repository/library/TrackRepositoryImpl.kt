package com.kelsos.mbrc.repository.library

import com.kelsos.mbrc.domain.Track
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Completable
import rx.Single
import javax.inject.Inject

class TrackRepositoryImpl
@Inject constructor() : TrackRepository {

  override fun getAllCursor(): Single<FlowCursorList<Track>> {
    TODO()
  }

  override fun getAlbumTracks(album: String, artist: String): Single<FlowCursorList<Track>> {
    TODO()
  }

  override fun getNonAlbumTracks(artist: String): Single<FlowCursorList<Track>> {
    TODO()
  }

  override fun getAndSaveRemote(): Single<FlowCursorList<Track>> {
    TODO()
  }

  override fun getRemote(): Completable {
    TODO()
  }

  override fun search(term: String): Single<FlowCursorList<Track>> {
    TODO()
  }

  override fun getGenreTrackPaths(genre: String): Single<List<String>> {
    TODO()
  }

  override fun getArtistTrackPaths(artist: String): Single<List<String>> {
    TODO()
  }

  override fun getAlbumTrackPaths(album: String, artist: String): Single<List<String>> {
    TODO()
  }
}
