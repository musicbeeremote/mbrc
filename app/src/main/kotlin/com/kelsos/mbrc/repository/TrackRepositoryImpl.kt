package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.data.library.Track_Table
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor() : TrackRepository {
  override fun getAllCursor(): Single<FlowCursorList<Track>> {
    return Single.create<FlowCursorList<Track>> {

      val modelQueriable = (select from Track::class)
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)

      val cursor = FlowCursorList.Builder<Track>(Track::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun getAlbumTracks(album: String): Single<FlowCursorList<Track>> {
    return Single.create<FlowCursorList<Track>> {
      val modelQueriable = (select from Track::class where Track_Table.album.like("%$album%"))
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)
      val cursor = FlowCursorList.Builder<Track>(Track::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun getNonAlbumTracks(artist: String): Single<FlowCursorList<Track>> {
    return Single.create<FlowCursorList<Track>> {
      val modelQueriable = (select from Track::class
          where Track_Table.album.`is`("")
          and Track_Table.artist.`is`(artist))
          .orderBy(Track_Table.album_artist, true)
          .orderBy(Track_Table.album, true)
          .orderBy(Track_Table.disc, true)
          .orderBy(Track_Table.trackno, true)

      val cursor = FlowCursorList.Builder<Track>(Track::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }
}
