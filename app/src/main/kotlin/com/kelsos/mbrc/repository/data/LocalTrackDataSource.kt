package com.kelsos.mbrc.repository.data


import com.kelsos.mbrc.data.library.Track
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import rx.Emitter
import rx.Observable
import javax.inject.Inject

class LocalTrackDataSource
@Inject constructor() : LocalDataSource<Track> {
  override fun deleteAll() {
    delete(Track::class).execute()
  }

  override fun saveAll(list: List<Track>) {
    val adapter = modelAdapter<Track>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<Track>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<Track>> {
    return Observable.fromEmitter({
      val modelQueriable = select from Track::class
      val cursor = FlowCursorList.Builder(Track::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onCompleted()
    }, Emitter.BackpressureMode.LATEST)

  }
}
