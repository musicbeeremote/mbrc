package com.kelsos.mbrc.content.library.albums


import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.content.library.albums.Album_Table.album
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.Track_Table
import com.kelsos.mbrc.extensions.escapeLike
import com.kelsos.mbrc.interfaces.data.LocalDataSource
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.innerJoin
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.kotlinextensions.on
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import com.raizlabs.android.dbflow.list.FlowQueryList
import com.raizlabs.android.dbflow.sql.language.OperatorGroup.clause
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

    database<RemoteDatabase>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<List<Album>> {
    return Observable.create {
      val modelQueriable = (select from Album::class)
          .orderBy(Album_Table.artist, true)
          .orderBy(album, true)
      it.onNext(modelQueriable.flowQueryList())
      it.onComplete()
    }
  }

  fun getAlbumsByArtist(artist: String): Single<List<Album>> {

    return Single.create<List<Album>> {
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

      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  override fun search(term: String): Single<List<Album>> {
    return Single.create<List<Album>> {
      val modelQueriable = (select from Album::class where album.like("%${term.escapeLike()}%"))
      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(Album::class.java).longValue() == 0L
    }
  }

  fun getAlbumsSorted(@Sorting.Fields order: Long, ascending: Boolean): Single<List<Album>> {
    val join = SQLite.select().from(Album::class)
        .innerJoin(Track::class)
        .on(
            Album_Table.album.withTable().eq(Track_Table.album.withTable())
                .and(Album_Table.artist.withTable().eq(Track_Table.album_artist.withTable()))
        )

    val sorted = when (order) {
      Sorting.ALBUM -> {
        join.orderBy(Album_Table.album.withTable(), ascending)
      }
      Sorting.ALBUM_ARTIST__ALBUM -> {
        join.orderBy(Album_Table.artist.withTable(), ascending)
            .orderBy(Album_Table.album.withTable(), ascending)
      }
      Sorting.ALBUM_ARTIST__YEAR__ALBUM -> {
        join.orderBy(Album_Table.artist.withTable(), ascending)
            .orderBy(Track_Table.year.withTable(), ascending)
            .orderBy(Album_Table.album.withTable(), ascending)
      }
      Sorting.ARTIST__ALBUM -> {
        join.orderBy(Track_Table.artist.withTable(), ascending)
            .orderBy(Album_Table.album.withTable(), ascending)
      }
      Sorting.GENRE__ALBUM_ARTIST__ALBUM -> {
        join.orderBy(Track_Table.genre.withTable(), ascending)
            .orderBy(Album_Table.artist.withTable(), ascending)
            .orderBy(Album_Table.album.withTable(), ascending)
      }
      Sorting.YEAR__ALBUM -> {
        join.orderBy(Track_Table.year.withTable(), ascending)
            .orderBy(Album_Table.album.withTable(), ascending)
      }
      Sorting.YEAR__ALBUM_ARTIST__ALBUM -> {
        join.orderBy(Track_Table.year.withTable(), ascending)
            .orderBy(Album_Table.artist.withTable(), ascending)
            .orderBy(Album_Table.album.withTable(), ascending)
      }
      else -> throw IllegalArgumentException("no such option")
    }

    return Single.just(FlowQueryList.Builder(Album::class.java)
        .modelQueriable(sorted.groupBy(Album_Table.album.withTable(), Album_Table.artist.withTable()))
        .build())
  }
}
