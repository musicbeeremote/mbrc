package com.kelsos.mbrc.content.library.genres

import com.kelsos.mbrc.RemoteDatabase
import com.kelsos.mbrc.di.modules.AppDispatchers
import com.kelsos.mbrc.extensions.escapeLike
import com.kelsos.mbrc.interfaces.data.LocalDataSource
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.from
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.kotlinextensions.select
import com.raizlabs.android.dbflow.kotlinextensions.where
import com.raizlabs.android.dbflow.sql.language.OperatorGroup.clause
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalGenreDataSource
@Inject constructor(private val dispatchers: AppDispatchers) : LocalDataSource<Genre> {
  override suspend fun deleteAll() = withContext(dispatchers.db) {
    delete(Genre::class).execute()
  }

  override suspend fun saveAll(list: List<Genre>) = withContext(dispatchers.db) {
    val adapter = modelAdapter<Genre>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
      .addAll(list)
      .build()

    database<RemoteDatabase>().executeTransaction(transaction)
  }

  override suspend fun loadAllCursor(): List<Genre> = withContext(dispatchers.db) {
    val query = (select from Genre::class).orderBy(Genre_Table.genre, true)
    return@withContext query.flowQueryList()
  }

  override suspend fun search(term: String): List<Genre> = withContext(dispatchers.db) {
    val query = (select from Genre::class where Genre_Table.genre.like("%${term.escapeLike()}%"))
      .orderBy(Genre_Table.genre, true)
    return@withContext query.flowQueryList()
  }

  override suspend fun isEmpty(): Boolean = withContext(dispatchers.db) {
    return@withContext SQLite.selectCountOf().from(Genre::class.java).longValue() == 0L
  }

  override suspend fun count(): Long = withContext(dispatchers.db) {
    return@withContext SQLite.selectCountOf().from(Genre::class.java).longValue()
  }

  override suspend fun removePreviousEntries(epoch: Long) {
    withContext(dispatchers.io) {
      SQLite.delete()
        .from(Genre::class.java)
        .where(clause(Genre_Table.date_added.lessThan(epoch)).or(Genre_Table.date_added.isNull))
        .execute()
    }
  }
}
