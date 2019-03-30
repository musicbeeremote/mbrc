package com.kelsos.mbrc.utils

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.koin.dsl.module

val testDispatcher = TestCoroutineDispatcher()

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
