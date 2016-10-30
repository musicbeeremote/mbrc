package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.NowPlaying
import com.kelsos.mbrc.data.NowPlaying_Table
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.kotlinextensions.orderBy
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.sql.language.OrderBy
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import rx.Emitter
import rx.Observable
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
}
