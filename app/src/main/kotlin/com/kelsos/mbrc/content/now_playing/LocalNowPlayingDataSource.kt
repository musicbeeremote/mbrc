package com.kelsos.mbrc.content.now_playing

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.content.now_playing.NowPlaying_Table.artist
import com.kelsos.mbrc.content.now_playing.NowPlaying_Table.title
import com.kelsos.mbrc.extensions.escapeLike
import com.kelsos.mbrc.interfaces.data.LocalDataSource
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.kotlinextensions.or
import com.raizlabs.android.dbflow.kotlinextensions.orderBy
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class LocalNowPlayingDataSource
@Inject constructor() : LocalDataSource<NowPlaying> {
  override fun deleteAll() {
    delete(NowPlaying::class).execute()
  }

  override fun saveAll(list: List<NowPlaying>) {
    val adapter = modelAdapter<NowPlaying>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<RemoteDatabase>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<NowPlaying>> {
    return Observable.create {
      val positionAscending = OrderBy.fromProperty(NowPlaying_Table.position).ascending()
      val modelQueriable = (select from NowPlaying::class orderBy positionAscending)
      val cursor = FlowCursorList.Builder(NowPlaying::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onComplete()
    }
  }

  override fun search(term: String): Single<FlowCursorList<NowPlaying>> {
    return Single.create<FlowCursorList<NowPlaying>> {
      val searchTerm = "%${term.escapeLike()}%"
      val modelQueriable = (select from NowPlaying::class where title.like(searchTerm) or artist.like(searchTerm))
      val cursor = FlowCursorList.Builder(NowPlaying::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }
  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(NowPlaying::class.java).longValue() == 0L
    }
  }
}
