package com.kelsos.mbrc.utils

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.koin.dsl.module

val testDispatcher = TestCoroutineDispatcher()
val testScope = TestCoroutineScope(testDispatcher)

val appCoroutineDispatchers = AppCoroutineDispatchers(
  testDispatcher,
  testDispatcher,
  testDispatcher,
  testDispatcher
)

val testDispatcherModule = module {
  single {
    appCoroutineDispatchers
  }
}
