package com.kelsos.mbrc.repository.data

import com.kelsos.mbrc.data.RadioStation
import com.kelsos.mbrc.data.RadioStation_Table
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

class LocalRadioDataSourceImpl
@Inject constructor() : LocalRadioDataSource {
  override fun deleteAll() {
    delete(RadioStation::class).execute()
  }

  override fun saveAll(list: List<RadioStation>) {
    val adapter = modelAdapter<RadioStation>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(list)
        .build()

    database<RadioStation>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<FlowCursorList<RadioStation>> {
    return Observable.create {
      val modelQueriable = (select from RadioStation::class)
      val cursor = FlowCursorList.Builder(RadioStation::class.java).modelQueriable(modelQueriable).build()
      it.onNext(cursor)
      it.onComplete()
    }
  }

  override fun search(term: String): Single<FlowCursorList<RadioStation>> {
    return Single.create<FlowCursorList<RadioStation>> {
      val modelQueriable = (select from RadioStation::class where RadioStation_Table.name.like("%${term.escapeLike()}%"))
      val cursor = FlowCursorList.Builder(RadioStation::class.java).modelQueriable(modelQueriable).build()
      it.onSuccess(cursor)
    }
  }

  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(RadioStation::class.java).count() == 0L
    }
  }
}
