package com.kelsos.mbrc.utils

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.koin.dsl.module

val testDispatcher = TestCoroutineDispatcher()

@OptIn(ExperimentalCoroutinesApi::class)
object TestDispatchers {
  val dispatchers = AppCoroutineDispatchers(
    testDispatcher,
    testDispatcher,
    testDispatcher,
    testDispatcher
  )
}

val testDispatcherModule = module {
  single { TestDispatchers.dispatchers }
}
