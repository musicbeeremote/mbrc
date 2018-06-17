package com.kelsos.mbrc.data

import com.kelsos.mbrc.DatabaseTransactionRunner
import javax.inject.Inject

class DatabaseTransactionRunnerImpl
@Inject
constructor(private val database: Database) : DatabaseTransactionRunner {
  override fun runInTransaction(action: () -> Unit) {
    database.runInTransaction(action)
  }
}
