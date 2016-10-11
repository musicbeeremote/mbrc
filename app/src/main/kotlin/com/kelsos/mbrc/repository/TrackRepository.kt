package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Track
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single

interface TrackRepository {
  fun getAllCursor(): Single<FlowCursorList<Track>>
  fun getAlbumTracks(album: String): Single<FlowCursorList<Track>>
  fun getNonAlbumTracks(artist: String): Single<FlowCursorList<Track>>
}
