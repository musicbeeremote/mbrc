package com.kelsos.mbrc.data

interface DatabaseTransactionRunner {
  fun runInTransaction(action: () -> Unit)
}