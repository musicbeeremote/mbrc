package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.data.NowPlaying_Table
import com.kelsos.mbrc.data.NowPlaying_Table.artist
import com.kelsos.mbrc.data.NowPlaying_Table.title
import com.kelsos.mbrc.data.library.Album
import com.kelsos.mbrc.extensions.escapeLike
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import rx.Emitter
import rx.Observable
import rx.Single
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

    database<NowPlaying>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<NowPlaying>> {
    return Observable.fromEmitter({
      val positionAscending = OrderBy.fromProperty(NowPlaying_Table.position).ascending()
      val modelQueriable = (select from NowPlaying::class orderBy positionAscending)
      val cursor = FlowCursorList.Builder(NowPlaying::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onCompleted()
    }, Emitter.BackpressureMode.LATEST)
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
      return@fromCallable SQLite.selectCountOf().from(NowPlaying::class.java).count() == 0L
    }
  }
}
