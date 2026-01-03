package com.kelsos.mbrc.core.common.utilities.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface AppCoroutineDispatchers {
  val main: CoroutineDispatcher
  val io: CoroutineDispatcher
  val database: CoroutineDispatcher
  val network: CoroutineDispatcher
}
