package com.kelsos.mbrc.utils

import com.kelsos.mbrc.common.utilities.AppDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.koin.dsl.module

val testDispatcher = TestCoroutineDispatcher()

@OptIn(ExperimentalCoroutinesApi::class)
object TestDispatchers {
  val dispatchers = AppDispatchers(
    testDispatcher,
    testDispatcher,
    testDispatcher,
    testDispatcher
  )
}

val testDispatcherModule = module {
  single { TestDispatchers.dispatchers }
}
