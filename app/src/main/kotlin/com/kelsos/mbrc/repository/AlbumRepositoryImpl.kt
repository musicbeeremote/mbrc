package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Album_Table
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.data.library.Track_Table
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Single
import javax.inject.Inject

class AlbumRepositoryImpl
@Inject constructor() :
    AlbumRepository {
  override fun getAlbumsByArtist(artist: String): Single<FlowCursorList<Album>> {
    return Single.create<FlowCursorList<Album>> {
      val modelQueriable = (select from Album::class
          leftOuterJoin Track::class
          on Track_Table.album.withTable().eq(Album_Table.album.withTable())
          where Track_Table.artist.withTable().like("%$artist%")
          groupBy Track_Table.artist.withTable())
          .orderBy(Album_Table.artist.withTable(), true)
          .orderBy(Album_Table.album.withTable(), true)
      val cursor = FlowCursorList.Builder(Album::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun getAllCursor(): Single<FlowCursorList<Album>> {
    return Single.create<FlowCursorList<Album>> {
      val modelQueriable = (select from Album::class)
          .orderBy(Album_Table.artist, true)
          .orderBy(Album_Table.album, true)
      val cursor = FlowCursorList.Builder(Album::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }
}
