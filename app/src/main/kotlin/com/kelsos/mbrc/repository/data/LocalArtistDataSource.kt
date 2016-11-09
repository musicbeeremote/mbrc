package com.kelsos.mbrc.repository.data


import com.kelsos.mbrc.data.library.Artist
import com.kelsos.mbrc.data.library.Artist_Table.artist
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.data.library.Track_Table
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import rx.Emitter
import rx.Observable
import rx.Single
import javax.inject.Inject

class LocalArtistDataSource
@Inject constructor() : LocalDataSource<Artist> {
  override fun deleteAll() {
    delete(Artist::class).execute()
  }

  override fun saveAll(list: List<Artist>) {
    val adapter = modelAdapter<Artist>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<Artist>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<Artist>> {
    return Observable.fromEmitter({
      val modelQueriable = (select from Artist::class).orderBy(artist, true)
      val cursor = FlowCursorList.Builder(Artist::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onCompleted()
    }, Emitter.BackpressureMode.LATEST)

  }

  fun getArtistByGenre(genre: String): Single<FlowCursorList<Artist>> {
    return Single.create {
      val modelQueriable = SQLite.select().distinct()
          .from<Artist>(Artist::class.java)
          .innerJoin<Track>(Track::class.java)
          .on(artist.withTable()
              .eq(Track_Table.artist.withTable()))
          .where(Track_Table.genre.`is`(genre))
          .orderBy(artist.withTable(), true).
          groupBy(artist.withTable())
      val cursor = FlowCursorList.Builder(Artist::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun search(term: String): Single<FlowCursorList<Artist>> {
    return Single.create {
      val modelQueriable = (select from Artist::class where artist.like("%$term%"))
      val cursor = FlowCursorList.Builder(Artist::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }
}
