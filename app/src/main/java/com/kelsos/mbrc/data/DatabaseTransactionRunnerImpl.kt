package com.kelsos.mbrc.data

class DatabaseTransactionRunnerImpl

constructor(private val database: Database) :
  DatabaseTransactionRunner {
  override fun runInTransaction(action: () -> Unit) {
    database.runInTransaction(action)
  }
}