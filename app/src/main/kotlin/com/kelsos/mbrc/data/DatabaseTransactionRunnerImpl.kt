package com.kelsos.mbrc.data

import com.kelsos.mbrc.DatabaseTransactionRunner

class DatabaseTransactionRunnerImpl(
  private val database: Database
) : DatabaseTransactionRunner {
  override fun runInTransaction(action: () -> Unit) {
    database.runInTransaction(action)
  }
}
