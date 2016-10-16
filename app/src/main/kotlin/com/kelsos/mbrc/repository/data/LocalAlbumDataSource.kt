package com.kelsos.mbrc.repository.data


import com.kelsos.mbrc.data.library.Album
import com.raizlabs.android.dbflow.kotlinextensions.*
import com.raizlabs.android.dbflow.list.FlowCursorList
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import rx.Emitter
import rx.Observable
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

    database<Album>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<Album>> {
    return Observable.fromEmitter({
      val modelQueriable = select from Album::class
      val cursor = FlowCursorList.Builder(Album::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onCompleted()
    }, Emitter.BackpressureMode.LATEST)

  }
}
