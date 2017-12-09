package com.kelsos.mbrc.content.library.artists

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.content.library.artists.Artist_Table.artist
import com.kelsos.mbrc.content.library.tracks.Track
import com.kelsos.mbrc.content.library.tracks.Track_Table
import com.kelsos.mbrc.extensions.escapeLike
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class LocalArtistDataSourceImpl
@Inject constructor() : LocalArtistDataSource {

  override fun deleteAll() {
    delete(Artist::class).execute()
  }

  override fun saveAll(list: List<Artist>) {
    val adapter = modelAdapter<Artist>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<RemoteDatabase>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<List<Artist>> {
    return Observable.create {
      val modelQueriable = (select from Artist::class).orderBy(artist, true)

      it.onNext(modelQueriable.flowQueryList())
      it.onComplete()
    }
  }

  override fun getArtistByGenre(genre: String): Single<List<Artist>> {
    return Single.create {
      val modelQueriable = SQLite.select().distinct()
          .from<Artist>(Artist::class.java)
          .innerJoin<Track>(Track::class.java)
          .on(artist.withTable()
              .eq(Track_Table.artist.withTable()))
          .where(Track_Table.genre.`is`(genre))
          .orderBy(artist.withTable(), true).
          groupBy(artist.withTable())

      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  override fun search(term: String): Single<List<Artist>> {
    return Single.create {
      val modelQueriable = (select from Artist::class where artist.like("%${term.escapeLike()}%"))

      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  override fun getAlbumArtists(): Single<List<Artist>> {
    return Single.create {
      val modelQueriable = SQLite.select().distinct()
          .from<Artist>(Artist::class.java)
          .innerJoin<Track>(Track::class.java)
          .on(artist.withTable().eq(Track_Table.artist.withTable()))
          .where(artist.withTable().`in`(Track_Table.album_artist.withTable()))
          .orderBy(artist.withTable(), true).
          groupBy(artist.withTable())

      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(Artist::class.java).longValue() == 0L
    }
  }
}
