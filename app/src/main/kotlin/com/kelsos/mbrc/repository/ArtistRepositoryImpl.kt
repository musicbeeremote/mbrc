package com.kelsos.mbrc.repository

import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Artist_Table
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.data.library.Track_Table
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Single
import javax.inject.Inject

class ArtistRepositoryImpl
@Inject constructor() : ArtistRepository {
  override fun getArtistByGenre(genre: String): Single<FlowCursorList<Artist>> {
    return Single.create {
      val modelQueriable = SQLite.select().distinct()
          .from<Artist>(Artist::class.java)
          .innerJoin<Track>(Track::class.java)
          .on(Artist_Table.artist.withTable()
              .eq(Track_Table.artist.withTable()))
          .where(Track_Table.genre.`is`(genre))
          .orderBy(Artist_Table.artist.withTable(), true).
          groupBy(Artist_Table.artist.withTable())
      val cursor = FlowCursorList.Builder(Artist::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun getAllCursor(): Single<FlowCursorList<Artist>> {
    return Single.create {
      val modelQueriable = (select from Artist::class).orderBy(Artist_Table.artist, true)
      val cursor = FlowCursorList.Builder(Artist::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }
}
