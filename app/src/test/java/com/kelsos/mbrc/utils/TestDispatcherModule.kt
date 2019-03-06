package com.kelsos.mbrc.utils

import com.kelsos.mbrc.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.koin.dsl.module

val testDispatcher = TestCoroutineDispatcher()

val testDispatcherModule = module {
  single {
    AppCoroutineDispatchers(
      testDispatcher,
      testDispatcher,
      testDispatcher,
      testDispatcher
    )
  }
}
