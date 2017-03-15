package com.kelsos.mbrc.repository.data


import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.data.library.Album_Table
import com.kelsos.mbrc.data.library.Album_Table.album
import com.kelsos.mbrc.data.library.Track
import com.kelsos.mbrc.data.library.Track_Table
import com.kelsos.mbrc.extensions.escapeLike
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.innerJoin
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.kotlinextensions.on
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.sql.language.ConditionGroup.clause
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class LocalAlbumDataSource
@Inject constructor() : LocalDataSource<Album> {
  override fun deleteAll() {
    delete(Album::class).execute()
  }

  override fun saveAll(list: List<Album>) {
    val adapter = modelAdapter<Album>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<Album>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<Album>> {
    return Observable.create {
      val modelQueriable = (select from Album::class)
          .orderBy(Album_Table.artist, true)
          .orderBy(album, true)
      val cursor = FlowCursorList.Builder(Album::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onComplete()
    }
  }

  fun getAlbumsByArtist(artist: String): Single<FlowCursorList<Album>> {

    return Single.create<FlowCursorList<Album>> {
      val selectAlbum = SQLite.select(Album_Table.album.withTable(), Album_Table.artist.withTable()).distinct()
      val artistOrAlbumArtist = clause(Track_Table.artist.withTable().`is`(artist))
          .or(Track_Table.album_artist.withTable().`is`(artist))
      val columns = clause(Track_Table.album.withTable().eq(Album_Table.album.withTable()))
          .and(Track_Table.album_artist.withTable().eq(Album_Table.artist.withTable()))
      val modelQueriable = (selectAlbum from Album::class
          innerJoin Track::class
          on columns
          where artistOrAlbumArtist)
          .orderBy(Album_Table.artist.withTable(), true)
          .orderBy(album.withTable(), true)
      val cursor = FlowCursorList.Builder(Album::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun search(term: String): Single<FlowCursorList<Album>> {
    return Single.create<FlowCursorList<Album>> {
      val modelQueriable = (select from Album::class where album.like("%${term.escapeLike()}%"))
      val cursor = FlowCursorList.Builder(Album::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(Album::class.java).count() == 0L
    }
  }
}
