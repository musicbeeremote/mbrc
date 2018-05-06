package com.kelsos.mbrc

interface DatabaseTransactionRunner {

  fun runInTransaction(action: () -> Unit)
}
