package com.kelsos.mbrc.content.playlists

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.content.playlists.Playlist_Table.name
import com.kelsos.mbrc.extensions.escapeLike
import com.kelsos.mbrc.interfaces.data.LocalDataSource
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

class LocalPlaylistDataSource
@Inject constructor(): LocalDataSource<Playlist> {
  override fun deleteAll() {
    delete(Playlist::class).execute()
  }

  override fun saveAll(list: List<Playlist>) {
    val adapter = modelAdapter<Playlist>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<RemoteDatabase>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<List<Playlist>> {
    return Observable.create {
      val modelQueriable = (select from Playlist::class)
      it.onNext(modelQueriable.flowQueryList())
      it.onComplete()
    }
  }

  override fun search(term: String): Single<List<Playlist>> {
    return Single.create<List<Playlist>> {
      val modelQueriable = (select from Playlist::class where name.like("%${term.escapeLike()}%"))
      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(Playlist::class.java).longValue() == 0L
    }
  }
}
