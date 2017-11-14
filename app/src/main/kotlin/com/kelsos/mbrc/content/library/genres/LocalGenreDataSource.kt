package com.kelsos.mbrc.content.library.genres

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.content.library.genres.Genre_Table.genre
import com.kelsos.mbrc.extensions.escapeLike
import com.kelsos.mbrc.interfaces.data.LocalDataSource
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

class LocalGenreDataSource
@Inject constructor() : LocalDataSource<Genre> {
  override fun deleteAll() {
    delete(Genre::class).execute()
  }

  override fun saveAll(list: List<Genre>) {
    val adapter = modelAdapter<Genre>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<RemoteDatabase>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<Genre>> {
    return Observable.create {
      val modelQueriable = (select from Genre::class).orderBy(Genre_Table.genre, true)
      val cursor = FlowCursorList.Builder(Genre::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onComplete()
    }

  }

  override fun search(term: String): Single<FlowCursorList<Genre>> {
    return Single.create<FlowCursorList<Genre>> {
      val modelQueriable = (select from Genre::class where genre.like("%${term.escapeLike()}%"))
          .orderBy(Genre_Table.genre, true)
      val cursor = FlowCursorList.Builder(Genre::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(Genre::class.java).longValue() == 0L
    }
  }
}
