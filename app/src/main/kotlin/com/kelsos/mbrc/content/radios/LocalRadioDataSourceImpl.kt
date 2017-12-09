package com.kelsos.mbrc.content.radios

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.extensions.escapeLike
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

    database<RemoteDatabase>().executeTransaction(transaction)
  }

  override fun loadAllCursor(): Observable<List<RadioStation>> {
    return Observable.create {
      val modelQueriable = (select from RadioStation::class)
      it.onNext(modelQueriable.flowQueryList())
      it.onComplete()
    }
  }

  override fun search(term: String): Single<List<RadioStation>> {
    return Single.create<List<RadioStation>> {
      val modelQueriable = (select from RadioStation::class where RadioStation_Table.name.like("%${term.escapeLike()}%"))
      it.onSuccess(modelQueriable.flowQueryList())
    }
  }

  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(RadioStation::class.java).longValue() == 0L
    }
  }
}
