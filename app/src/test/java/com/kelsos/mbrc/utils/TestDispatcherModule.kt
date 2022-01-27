package com.kelsos.mbrc.utils

import com.kelsos.mbrc.common.utilities.AppCoroutineDispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import org.koin.dsl.module

private val scheduler = TestCoroutineScheduler()

val mainTestDispatcher = StandardTestDispatcher(scheduler)
val ioTestDispatcher = StandardTestDispatcher(scheduler, name = "io")
val dbTestDispatcher = StandardTestDispatcher(scheduler, name = "db")
val networkTestDispatcher = StandardTestDispatcher(scheduler, name = "network")

val appCoroutineDispatchers =
  AppCoroutineDispatchers(
    main = mainTestDispatcher,
    io = ioTestDispatcher,
    database = dbTestDispatcher,
    network = networkTestDispatcher,
  )

val testDispatcherModule =
  module {
    single {
      appCoroutineDispatchers
    }
  }
