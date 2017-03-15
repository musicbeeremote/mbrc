package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.Playlist
import com.kelsos.mbrc.data.Playlist_Table.name
import com.kelsos.mbrc.extensions.escapeLike
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import com.raizlabs.android.dbflow.list.FlowCursorList
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

    database<Playlist>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<Playlist>> {
    return Observable.create {
      val modelQueriable = (select from Playlist::class)
      val cursor = FlowCursorList.Builder(Playlist::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onComplete()
    }
  }

  override fun search(term: String): Single<FlowCursorList<Playlist>> {
    return Single.create<FlowCursorList<Playlist>> {
      val modelQueriable = (select from Playlist::class where name.like("%${term.escapeLike()}%"))
      val cursor = FlowCursorList.Builder(Playlist::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(Playlist::class.java).count() == 0L
    }
  }
}
