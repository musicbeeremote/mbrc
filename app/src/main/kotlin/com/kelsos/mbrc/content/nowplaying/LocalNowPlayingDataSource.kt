package com.kelsos.mbrc.content.nowplaying

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.content.nowplaying.NowPlaying_Table.artist
import com.kelsos.mbrc.content.nowplaying.NowPlaying_Table.title
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

  override fun loadAllCursor(): Observable<List<NowPlaying>> {
    return Observable.create {
      val positionAscending = OrderBy.fromProperty(NowPlaying_Table.position).ascending()
      val modelQueriable = (select from NowPlaying::class orderBy positionAscending)

      it.onNext(modelQueriable.flowQueryList())
      it.onComplete()
    }
  }

  override fun search(term: String): Single<List<NowPlaying>> {
    return Single.create<List<NowPlaying>> {
      val searchTerm = "%${term.escapeLike()}%"
      val modelQueriable = (select from NowPlaying::class where title.like(searchTerm) or artist.like(searchTerm))
      it.onSuccess(modelQueriable.flowQueryList())
    }
  }
  override fun isEmpty(): Single<Boolean> {
    return Single.fromCallable {
      return@fromCallable SQLite.selectCountOf().from(NowPlaying::class.java).longValue() == 0L
    }
  }
}
