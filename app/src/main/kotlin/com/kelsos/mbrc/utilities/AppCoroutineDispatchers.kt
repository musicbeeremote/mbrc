package com.kelsos.mbrc.utilities

import kotlinx.coroutines.experimental.CoroutineDispatcher

data class AppCoroutineDispatchers(
  val main: CoroutineDispatcher,
  val disk: CoroutineDispatcher,
  val database: CoroutineDispatcher,
  val network: CoroutineDispatcher
)