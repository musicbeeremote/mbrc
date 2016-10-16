package com.kelsos.mbrc.repository.data


import com.kelsos.mbrc.data.library.Artist
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import rx.Emitter
import rx.Observable
import javax.inject.Inject

class LocalArtistDataSource
@Inject constructor() : LocalDataSource<Artist> {
  override fun deleteAll() {
    delete(Artist::class).execute()
  }

  override fun saveAll(list: List<Artist>) {
    val adapter = modelAdapter<Artist>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<Artist>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<Artist>> {
    return Observable.fromEmitter({
      val modelQueriable = select from Artist::class
      val cursor = FlowCursorList.Builder(Artist::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onCompleted()
    }, Emitter.BackpressureMode.LATEST)

  }
}
