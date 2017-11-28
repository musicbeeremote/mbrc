package com.kelsos.mbrc.content.library

import com.kelsos.mbrc.RemoteDatabase
import com.raizlabs.android.dbflow.kotlinextensions.database
import com.raizlabs.android.dbflow.kotlinextensions.modelAdapter
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction
import javax.inject.Inject

class UpdatedDataSource
@Inject
constructor() {
  fun addUpdated(paths: List<String>, dateInserted: Long) {
    val updated = paths.map { Updated(path = it, dateInserted = dateInserted) }.toList()
    val adapter = modelAdapter<Updated>()

    val transaction = FastStoreModelTransaction.insertBuilder(adapter)
        .addAll(updated)
        .build()

    database<RemoteDatabase>().executeTransaction(transaction)
  }

  fun getPathInsertedAtEpoch(epoch: Long): List<String> {
    return SQLite.select()
        .from(Updated::class.java)
        .where(Updated_Table.date_inserted.`is`(epoch))
        .queryList()
        .map { it.path }
  }

  fun deleteAll() {
    SQLite.delete()
        .from(Updated::class.java)
        .execute()
  }
}
